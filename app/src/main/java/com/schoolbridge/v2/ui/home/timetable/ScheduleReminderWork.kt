package com.schoolbridge.v2.ui.home.timetable

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.schoolbridge.v2.MainActivity
import com.schoolbridge.v2.R
import com.schoolbridge.v2.data.dto.academic.MobilePersonalTimetablePlanDto
import com.schoolbridge.v2.data.dto.academic.MobileTimetableEntryDto
import com.schoolbridge.v2.data.dto.academic.MobileTimetableResponseDto
import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto
import com.schoolbridge.v2.data.dto.message.MobileMessageDto
import com.schoolbridge.v2.data.dto.message.MobileThreadCallSummaryDto
import com.schoolbridge.v2.data.remote.MessageApiServiceImpl
import com.schoolbridge.v2.data.remote.TimetableApiServiceImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import kotlinx.coroutines.runBlocking
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import java.util.Locale
import java.util.concurrent.TimeUnit

private const val REMINDER_CHANNEL_ID = "schoolbridge_schedule_reminders"
private const val REFRESH_WORK_NAME = "schoolbridge_schedule_reminder_refresh"
private const val REMINDER_LEAD_MINUTES = 5L
private const val REMINDER_LOOKAHEAD_DAYS = 7L
private const val REFRESH_INTERVAL_HOURS = 6L

private const val KEY_NOTIFICATION_ID = "notification_id"
private const val KEY_TITLE = "title"
private const val KEY_BODY = "body"
private const val KEY_THREAD_ID = "thread_id"
private const val KEY_CALL_MESSAGE_ID = "call_message_id"
private const val KEY_TARGET_SCREEN = "target_screen"
private const val KEY_START_MILLIS = "start_millis"
private const val KEY_MESSAGE_ID = "message_id"
private const val KEY_ACTION_IDS = "action_ids"
private const val KEY_ACTION_LABELS = "action_labels"
private const val ACTION_OPEN_REMINDER = "com.schoolbridge.v2.action.OPEN_REMINDER"
private const val ACTION_SNOOZE_REMINDER = "com.schoolbridge.v2.action.SNOOZE_REMINDER"
private const val ACTION_PERFORM_MESSAGE_ACTION = "com.schoolbridge.v2.action.PERFORM_MESSAGE_ACTION"
private const val SNOOZE_MINUTES = 5L

internal enum class ReminderTargetScreen {
    TIMETABLE,
    MESSAGE_THREAD
}

internal data class ReminderPayload(
    val uniqueId: String,
    val title: String,
    val body: String,
    val startAt: LocalDateTime,
    val threadId: String? = null,
    val callMessageId: String? = null,
    val messageId: String? = null,
    val actionIds: List<String> = emptyList(),
    val actionLabels: List<String> = emptyList(),
    val targetScreen: ReminderTargetScreen = ReminderTargetScreen.TIMETABLE
) {
    fun toWorkData(): Data = Data.Builder()
        .putString(KEY_NOTIFICATION_ID, uniqueId)
        .putString(KEY_TITLE, title)
        .putString(KEY_BODY, body)
        .putString(KEY_THREAD_ID, threadId)
        .putString(KEY_CALL_MESSAGE_ID, callMessageId)
        .putString(KEY_MESSAGE_ID, messageId)
        .putStringArray(KEY_ACTION_IDS, actionIds.toTypedArray())
        .putStringArray(KEY_ACTION_LABELS, actionLabels.toTypedArray())
        .putString(KEY_TARGET_SCREEN, targetScreen.name)
        .putLong(KEY_START_MILLIS, startAt.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli())
        .build()
}

object ScheduleReminderScheduler {
    fun start(context: Context) {
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniquePeriodicWork(
            REFRESH_WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            PeriodicWorkRequestBuilder<ScheduleReminderRefreshWorker>(
                REFRESH_INTERVAL_HOURS, TimeUnit.HOURS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 15, TimeUnit.MINUTES)
                .build()
        )
        workManager.enqueueUniqueWork(
            "${REFRESH_WORK_NAME}_immediate",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<ScheduleReminderRefreshWorker>()
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
        )
    }

    internal fun enqueueReminder(context: Context, payload: ReminderPayload) {
        val now = LocalDateTime.now()
        val remindAt = payload.startAt.minusMinutes(REMINDER_LEAD_MINUTES)
        val delay = when {
            remindAt.isAfter(now) -> Duration.between(now, remindAt).toMillis()
            payload.startAt.isAfter(now) -> 0L
            else -> return
        }

        WorkManager.getInstance(context).enqueueUniqueWork(
            "schedule_reminder_${payload.uniqueId}",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<ScheduleReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(payload.toWorkData())
                .build()
        )
    }

    internal fun enqueueReminderData(
        context: Context,
        uniqueId: String,
        data: Data,
        delayMillis: Long
    ) {
        WorkManager.getInstance(context).enqueueUniqueWork(
            "schedule_reminder_$uniqueId",
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequestBuilder<ScheduleReminderWorker>()
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()
        )
    }
}

class ScheduleReminderRefreshWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val sessionManager = UserSessionManager(applicationContext)
        val token = sessionManager.getAuthToken() ?: return Result.success()
        if (token.isBlank()) return Result.success()

        return runCatching {
            val timetable = TimetableApiServiceImpl(sessionManager).getTimetable()
            val threads = MessageApiServiceImpl(sessionManager).getMessageThreads()
            buildReminderPayloads(applicationContext, timetable, threads).forEach { payload ->
                ScheduleReminderScheduler.enqueueReminder(applicationContext, payload)
            }
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }
}

class ScheduleReminderWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val title = inputData.getString(KEY_TITLE) ?: return Result.failure()
        val body = inputData.getString(KEY_BODY) ?: return Result.failure()
        val notificationId = inputData.getString(KEY_NOTIFICATION_ID)?.hashCode() ?: return Result.failure()
        val threadId = inputData.getString(KEY_THREAD_ID)
        val callMessageId = inputData.getString(KEY_CALL_MESSAGE_ID)
        val messageId = inputData.getString(KEY_MESSAGE_ID)
        val actionIds = inputData.getStringArray(KEY_ACTION_IDS).orEmpty().toList()
        val actionLabels = inputData.getStringArray(KEY_ACTION_LABELS).orEmpty().toList()
        val targetScreen = inputData.getString(KEY_TARGET_SCREEN)
            ?.let { runCatching { ReminderTargetScreen.valueOf(it) }.getOrNull() }
            ?: ReminderTargetScreen.TIMETABLE

        ensureNotificationChannel(applicationContext)

        val openIntent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            when (targetScreen) {
                ReminderTargetScreen.MESSAGE_THREAD -> {
                    putExtra(MainActivity.EXTRA_OPEN_THREAD_ID, threadId)
                    putExtra(MainActivity.EXTRA_OPEN_CALL_MESSAGE_ID, callMessageId)
                }
                ReminderTargetScreen.TIMETABLE -> {
                    putExtra(MainActivity.EXTRA_OPEN_SCHEDULE, true)
                }
            }
        }
        val contentIntent = PendingIntent.getActivity(
            applicationContext,
            notificationId,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openActionIntent = Intent(applicationContext, ScheduleReminderActionReceiver::class.java).apply {
            action = ACTION_OPEN_REMINDER
            putExtras(inputData.toBundle())
            putExtra(KEY_NOTIFICATION_ID, inputData.getString(KEY_NOTIFICATION_ID))
        }
        val openActionPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 1,
            openActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val snoozeIntent = Intent(applicationContext, ScheduleReminderActionReceiver::class.java).apply {
            action = ACTION_SNOOZE_REMINDER
            putExtras(inputData.toBundle())
            putExtra(KEY_NOTIFICATION_ID, inputData.getString(KEY_NOTIFICATION_ID))
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationId + 2,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val actionLabel = when {
            targetScreen == ReminderTargetScreen.MESSAGE_THREAD && callMessageId != null -> "Join now"
            targetScreen == ReminderTargetScreen.MESSAGE_THREAD -> "Open thread"
            else -> "Open schedule"
        }
        val builder = NotificationCompat.Builder(applicationContext, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher_poisson)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(
                if (targetScreen == ReminderTargetScreen.MESSAGE_THREAD) {
                    if (callMessageId != null) NotificationCompat.CATEGORY_CALL else NotificationCompat.CATEGORY_MESSAGE
                } else {
                    NotificationCompat.CATEGORY_REMINDER
                }
            )
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setAutoCancel(true)
            .setContentIntent(contentIntent)
            .addAction(0, actionLabel, openActionPendingIntent)

        if (actionIds.isNotEmpty() && messageId != null && threadId != null) {
            actionIds.zip(actionLabels).take(2).forEachIndexed { index, (actionId, label) ->
                val actionIntent = Intent(applicationContext, ScheduleReminderActionReceiver::class.java).apply {
                    action = ACTION_PERFORM_MESSAGE_ACTION
                    putExtras(inputData.toBundle())
                    putExtra(KEY_NOTIFICATION_ID, inputData.getString(KEY_NOTIFICATION_ID))
                    putExtra("selected_action_id", actionId)
                    putExtra("selected_action_label", label)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    applicationContext,
                    notificationId + 10 + index,
                    actionIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                builder.addAction(0, label, pendingIntent)
            }
        } else {
            builder.addAction(0, "Remind in 5 min", snoozePendingIntent)
        }

        NotificationManagerCompat.from(applicationContext).notify(notificationId, builder.build())
        return Result.success()
    }
}

class ScheduleReminderActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        val notificationId = intent.getStringExtra(KEY_NOTIFICATION_ID)?.hashCode() ?: return

        when (action) {
            ACTION_OPEN_REMINDER -> {
                val openIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra(MainActivity.EXTRA_OPEN_THREAD_ID, intent.getStringExtra(KEY_THREAD_ID))
                    putExtra(MainActivity.EXTRA_OPEN_CALL_MESSAGE_ID, intent.getStringExtra(KEY_CALL_MESSAGE_ID))
                    putExtra(
                        MainActivity.EXTRA_OPEN_SCHEDULE,
                        intent.getStringExtra(KEY_TARGET_SCREEN) == ReminderTargetScreen.TIMETABLE.name
                    )
                }
                context.startActivity(openIntent)
                NotificationManagerCompat.from(context).cancel(notificationId)
            }

            ACTION_SNOOZE_REMINDER -> {
                val data = intent.extras?.toData() ?: return
                ScheduleReminderScheduler.enqueueReminderData(
                    context = context,
                    uniqueId = "${intent.getStringExtra(KEY_NOTIFICATION_ID)}_snooze",
                    data = data,
                    delayMillis = TimeUnit.MINUTES.toMillis(SNOOZE_MINUTES)
                )
                NotificationManagerCompat.from(context).cancel(notificationId)
            }

            ACTION_PERFORM_MESSAGE_ACTION -> {
                val threadId = intent.getStringExtra(KEY_THREAD_ID) ?: return
                val messageId = intent.getStringExtra(KEY_MESSAGE_ID) ?: return
                val selectedActionId = intent.getStringExtra("selected_action_id") ?: return
                val selectedActionLabel = intent.getStringExtra("selected_action_label") ?: selectedActionId
                val sessionManager = UserSessionManager(context)
                val conversationId = threadId.toLongOrNull() ?: return
                val senderId = sessionManager.getCurrentUserIdSync()?.toLongOrNull() ?: return
                val reply = formatNotificationActionReply(selectedActionId, selectedActionLabel)
                runBlocking {
                    MessageApiServiceImpl(sessionManager).sendMessage(
                        conversationId = conversationId,
                        senderId = senderId,
                        content = reply
                    )
                }
                NotificationInteractionStore.markMessageHandled(context, messageId)
                selectedActionId.toMeetingDecisionOrNull()?.let { decision ->
                    NotificationInteractionStore.saveMeetingDecision(context, threadId, decision)
                }
                NotificationManagerCompat.from(context).cancel(notificationId)
            }
        }
    }
}

class ScheduleReminderBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED ||
            intent?.action == Intent.ACTION_MY_PACKAGE_REPLACED
        ) {
            ScheduleReminderScheduler.start(context)
        }
    }
}

private fun ensureNotificationChannel(context: Context) {
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (manager.getNotificationChannel(REMINDER_CHANNEL_ID) != null) return
    manager.createNotificationChannel(
        NotificationChannel(
            REMINDER_CHANNEL_ID,
            "Schedule reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Upcoming classes, meetings, calls, and school moments."
        }
    )
}

private fun buildReminderPayloads(
    context: Context,
    timetable: MobileTimetableResponseDto,
    threads: List<MobileMessageThreadDto>
): List<ReminderPayload> {
    val now = LocalDateTime.now()
    val endWindow = now.plusDays(REMINDER_LOOKAHEAD_DAYS)
    val payloads = mutableListOf<ReminderPayload>()

    timetable.entries
        .asSequence()
        .flatMap { entry -> entry.nextOccurrences(now.toLocalDate(), REMINDER_LOOKAHEAD_DAYS).asSequence().map { entry to it } }
        .filter { (_, startAt) -> startAt.isAfter(now) && startAt.isBefore(endWindow) }
        .forEach { (entry, startAt) ->
            val targetName = entry.studentName?.substringBefore(" ")
            val body = buildString {
                append("${entry.title} starts at ${startAt.toLocalTime().toString().take(5)}")
                entry.room?.takeIf { it.isNotBlank() }?.let { append(" in $it") }
                entry.teacher?.takeIf { it.isNotBlank() }?.let { append(" with $it") }
                targetName?.let { append(". $it is on this schedule.") }
            }
            payloads += ReminderPayload(
                uniqueId = "class_${entry.id}_${startAt}",
                title = when (entry.type.uppercase(Locale.getDefault())) {
                    "TEST" -> "Assessment coming up"
                    else -> "Class starts soon"
                },
                body = body,
                startAt = startAt
            )
        }

    timetable.personalPlans
        .mapNotNull { plan -> plan.toReminderPayload(now, endWindow) }
        .forEach(payloads::add)

    threads.forEach { thread ->
        thread.calls.forEach { call ->
            call.toReminderPayload(context, thread, now, endWindow)?.let(payloads::add)
        }
    }

    threads.forEach { thread ->
        thread.messages
            .filter { it.isUnread && it.actions.isNotEmpty() }
            .mapNotNull { it.toActionNotificationPayload(context, thread, now) }
            .forEach(payloads::add)
    }

    return payloads.distinctBy { it.uniqueId }
}

private fun MobileTimetableEntryDto.nextOccurrences(from: LocalDate, daysAhead: Long): List<LocalDateTime> {
    val day = runCatching { DayOfWeek.valueOf(dayOfWeek) }.getOrDefault(from.dayOfWeek)
    val start = LocalTime.parse(startTime)
    val firstDate = from.with(TemporalAdjusters.nextOrSame(day))
    return (0..daysAhead / 7)
        .map { firstDate.plusWeeks(it) }
        .map { it.atTime(start) }
}

private fun MobilePersonalTimetablePlanDto.toReminderPayload(
    now: LocalDateTime,
    endWindow: LocalDateTime
): ReminderPayload? {
    val startAt = LocalDate.parse(date).atTime(LocalTime.parse(startTime))
    if (!startAt.isAfter(now) || !startAt.isBefore(endWindow)) return null
    val body = buildString {
        append("$title starts at ${startAt.toLocalTime().toString().take(5)}.")
        description?.takeIf { it.isNotBlank() }?.let { append(" $it") }
        if (participantLabels.isNotEmpty()) {
            append(" With ${participantLabels.joinToString(", ")}.")
        }
    }
    return ReminderPayload(
        uniqueId = "personal_$id",
        title = "Your plan starts soon",
        body = body,
        startAt = startAt
    )
}

private fun MobileThreadCallSummaryDto.toReminderPayload(
    context: Context,
    thread: MobileMessageThreadDto,
    now: LocalDateTime,
    endWindow: LocalDateTime
): ReminderPayload? {
    val startAt = scheduledFor?.let(::parseOffsetDateTimeForDevice) ?: return null
    if (!startAt.isAfter(now) || !startAt.isBefore(endWindow)) return null
    if (NotificationInteractionStore.getMeetingDecision(context, thread.id) == MeetingDecision.DECLINED) {
        return null
    }
    val body = buildString {
        append("$title starts at ${startAt.toLocalTime().toString().take(5)} in ${thread.topic}.")
        participantSummary?.takeIf { it.isNotBlank() }?.let { append(" $it") }
        note?.takeIf { it.isNotBlank() }?.let { append(" $it") }
    }
    return ReminderPayload(
        uniqueId = "thread_call_$id",
        title = if (type.equals("VIDEO", ignoreCase = true)) "Video meeting starts soon" else "School call starts soon",
        body = body,
        startAt = startAt,
        threadId = thread.id,
        callMessageId = relatedMessageId ?: "call_$id",
        targetScreen = ReminderTargetScreen.MESSAGE_THREAD
    )
}

private fun MobileMessageDto.toActionNotificationPayload(
    context: Context,
    thread: MobileMessageThreadDto,
    now: LocalDateTime
): ReminderPayload? {
    if (NotificationInteractionStore.isMessageHandled(context, id)) return null
    val safeActions = actions
        .filter { it.actionId in SAFE_NOTIFICATION_ACTION_IDS }
        .take(2)
    if (safeActions.isEmpty()) return null
    return ReminderPayload(
        uniqueId = "message_action_$id",
        title = title?.ifBlank { null } ?: thread.topic,
        body = content,
        startAt = now.plusSeconds(1),
        threadId = thread.id,
        messageId = id,
        actionIds = safeActions.map { it.actionId },
        actionLabels = safeActions.map { it.label },
        targetScreen = ReminderTargetScreen.MESSAGE_THREAD
    )
}

private fun parseOffsetDateTimeForDevice(raw: String): LocalDateTime? = runCatching {
    OffsetDateTime.parse(raw)
        .atZoneSameInstant(ZoneId.systemDefault())
        .toLocalDateTime()
}.getOrNull()

private val SAFE_NOTIFICATION_ACTION_IDS = setOf(
    "acknowledge",
    "yes",
    "no",
    "not_sure",
    "mark_paid"
)

private fun formatNotificationActionReply(actionId: String, fallbackLabel: String): String = when (actionId.lowercase()) {
    "yes" -> "Yes, I'll be there"
    "no" -> "No, I can't make it"
    "not_sure" -> "I am not sure yet"
    "acknowledge" -> "I have seen this notice"
    "mark_paid" -> "I've completed the payment"
    else -> fallbackLabel
}

private fun String.toMeetingDecisionOrNull(): MeetingDecision? = when (lowercase()) {
    "yes" -> MeetingDecision.ATTENDING
    "no" -> MeetingDecision.DECLINED
    "not_sure" -> MeetingDecision.MAYBE
    else -> null
}

private fun Data.toBundle() = android.os.Bundle().apply {
    keyValueMap.forEach { (key, value) ->
        when (value) {
            is String -> putString(key, value)
            is Long -> putLong(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
        }
    }
}

private fun android.os.Bundle.toData(): Data = Data.Builder().apply {
    keySet().forEach { key ->
        when (val value = get(key)) {
            is String -> putString(key, value)
            is Long -> putLong(key, value)
            is Int -> putInt(key, value)
            is Boolean -> putBoolean(key, value)
        }
    }
}.build()
