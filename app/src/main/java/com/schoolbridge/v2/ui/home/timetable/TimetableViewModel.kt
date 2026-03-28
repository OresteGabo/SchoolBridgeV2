package com.schoolbridge.v2.ui.home.timetable

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.academic.CreatePersonalTimetablePlanRequestDto
import com.schoolbridge.v2.data.dto.academic.MobilePersonalTimetablePlanDto
import com.schoolbridge.v2.data.dto.academic.MobileTimetableEntryDto
import com.schoolbridge.v2.data.dto.academic.MobileTimetableResponseDto
import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto
import com.schoolbridge.v2.data.dto.message.MobileThreadCallSummaryDto
import com.schoolbridge.v2.data.repository.interfaces.MessagingRepository
import com.schoolbridge.v2.data.repository.interfaces.TimetableRepository
import com.schoolbridge.v2.domain.academic.AttendanceStatus
import com.schoolbridge.v2.domain.academic.TodayCourse
import com.schoolbridge.v2.domain.academic.TimetableEntry
import com.schoolbridge.v2.domain.academic.TimetableEntryType
import com.schoolbridge.v2.domain.academic.TimetableTemplateEntry
import com.schoolbridge.v2.domain.academic.toTimetableEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.format.TextStyle
import java.util.Locale

enum class AgendaItemKind {
    CLASS,
    ASSESSMENT,
    MEETING,
    CALL,
    ANNOUNCEMENT,
    PERSONAL
}

enum class AgendaItemOrigin {
    OFFICIAL,
    THREAD_PLAN,
    PERSONAL_PLAN
}

enum class AgendaDensity {
    COMFORTABLE,
    COMPACT
}

enum class PersonalPlanType {
    STUDY_BLOCK,
    HOMEWORK,
    GROUP_WORK,
    PROJECT_MILESTONE,
    CLUB_ACTIVITY,
    MEETING,
    REMINDER
}

data class AgendaItemUi(
    val id: String,
    val start: LocalDateTime,
    val end: LocalDateTime,
    val title: String,
    val subtitle: String,
    val badge: String,
    val kind: AgendaItemKind,
    val sourceLabel: String,
    val schoolName: String? = null,
    val room: String? = null,
    val studentName: String? = null,
    val note: String? = null,
    val statusLabel: String? = null,
    val ctaLabel: String? = null,
    val isImportant: Boolean = false,
    val isOwnedByCurrentUser: Boolean = false,
    val origin: AgendaItemOrigin = AgendaItemOrigin.OFFICIAL
)

data class TimetableStudent(
    val id: String,
    val name: String,
    val schoolId: String? = null,
    val schoolName: String? = null
)

data class TimetableUiState(
    val isLoading: Boolean = false,
    val audience: String = "GENERAL",
    val scopeLabel: String? = null,
    val students: List<TimetableStudent> = emptyList(),
    val selectedStudentIds: Set<String> = emptySet(),
    val templates: List<TimetableTemplateEntry> = emptyList(),
    val plannedItems: List<AgendaItemUi> = emptyList(),
    val personalPlans: List<AgendaItemUi> = emptyList(),
    val errorMessage: String? = null
) {
    fun weeklyEntries(startOfWeek: LocalDate): List<TimetableEntry> =
        filteredTemplates().map { it.toTimetableEntry(startOfWeek) }.sortedBy { it.start }

    fun dailyEntries(date: LocalDate): List<TimetableEntry> =
        filteredTemplates()
            .filter { it.dayOfWeek == date.dayOfWeek }
            .map { template ->
                TimetableEntry(
                    id = template.id.hashCode(),
                    start = date.atTime(template.startTime),
                    end = date.atTime(template.endTime),
                    title = template.title,
                    room = template.room,
                    teacher = template.teacher,
                    type = template.type,
                    studentId = template.studentId,
                    studentName = template.studentName,
                    note = template.note,
                    schoolId = template.schoolId,
                    schoolName = template.schoolName,
                    roleContext = template.roleContext
                )
            }
            .sortedBy { it.start }

    fun dailyAgenda(
        date: LocalDate,
        includedKinds: Set<AgendaItemKind> = AgendaItemKind.entries.toSet(),
        showOnlyMine: Boolean = false
    ): List<AgendaItemUi> =
        (dailyEntries(date).map { it.toAgendaItem() } + filteredPlannedItems(date, showOnlyMine) + filteredPersonalPlans(date))
            .filter { !showOnlyMine || it.isOwnedByCurrentUser }
            .filter { it.kind in includedKinds }
            .sortedBy { it.start }

    fun upcomingHighlights(
        from: LocalDateTime = LocalDateTime.now(),
        includedKinds: Set<AgendaItemKind> = AgendaItemKind.entries.toSet(),
        showOnlyMine: Boolean = false
    ): List<AgendaItemUi> =
        ((0..14).flatMap { offset -> dailyAgenda(from.toLocalDate().plusDays(offset.toLong()), includedKinds, showOnlyMine) })
            .filter { it.end.isAfter(from) }
            .sortedBy { it.start }
            .take(3)

    fun nowAndNext(
        from: LocalDateTime = LocalDateTime.now(),
        includedKinds: Set<AgendaItemKind> = AgendaItemKind.entries.toSet(),
        showOnlyMine: Boolean = false
    ): Pair<AgendaItemUi?, AgendaItemUi?> {
        val agenda = ((0..2).flatMap { offset -> dailyAgenda(from.toLocalDate().plusDays(offset.toLong()), includedKinds, showOnlyMine) })
            .sortedBy { it.start }
        val nowItem = agenda.firstOrNull { !it.start.isAfter(from) && it.end.isAfter(from) }
        val nextItem = agenda.firstOrNull { it.start.isAfter(from) && it.id != nowItem?.id }
        return nowItem to nextItem
    }

    fun upcomingDeadlines(
        from: LocalDateTime = LocalDateTime.now(),
        includedKinds: Set<AgendaItemKind> = AgendaItemKind.entries.toSet(),
        showOnlyMine: Boolean = false
    ): List<AgendaItemUi> =
        ((0..14).flatMap { offset -> dailyAgenda(from.toLocalDate().plusDays(offset.toLong()), includedKinds, showOnlyMine) })
            .filter { it.start.isAfter(from) }
            .filter { it.isImportant || it.kind == AgendaItemKind.ASSESSMENT || it.statusLabel != null }
            .sortedBy { it.start }
            .take(4)

    fun todayCourses(date: LocalDate = LocalDate.now()): List<TodayCourse> =
        dailyEntries(date).map { entry ->
            TodayCourse(
                subject = entry.title,
                startTime = entry.start.toLocalTime().toString().take(5),
                endTime = entry.end.toLocalTime().toString().take(5),
                teacher = entry.teacher.ifBlank { "School team" },
                location = entry.room,
                attendanceStatus = AttendanceStatus.UNMARKED,
                studentId = entry.studentId,
                studentName = entry.studentName
            )
        }

    fun selectedSchoolLabels(showOnlyMine: Boolean = false): List<String> {
        val learnerSchools = if (showOnlyMine) {
            emptyList()
        } else if (selectedStudentIds.isEmpty()) {
            students.mapNotNull { it.schoolName }
        } else {
            students.filter { it.id in selectedStudentIds }.mapNotNull { it.schoolName }
        }
        val teachingSchools = templates
            .filter { it.roleContext == "TEACHER_SELF" }
            .mapNotNull { it.schoolName }
        return (teachingSchools + learnerSchools).distinct()
    }

    fun hasTeachingSchedule(): Boolean = templates.any { it.roleContext == "TEACHER_SELF" }

    fun nextDateWithEvent(
        fromDate: LocalDate,
        includedKinds: Set<AgendaItemKind> = AgendaItemKind.entries.toSet(),
        showOnlyMine: Boolean = false
    ): LocalDate? =
        (0..30)
            .map { fromDate.plusDays(it.toLong()) }
            .firstOrNull { date ->
                dailyAgenda(date, includedKinds, showOnlyMine).isNotEmpty()
            }

    private fun filteredTemplates(): List<TimetableTemplateEntry> =
        if (selectedStudentIds.isEmpty()) {
            templates
        } else {
            templates.filter { it.studentId == null || it.studentId in selectedStudentIds }
        }

    private fun filteredPlannedItems(date: LocalDate, showOnlyMine: Boolean): List<AgendaItemUi> =
        plannedItems.filter { it.start.toLocalDate() == date && (!showOnlyMine || it.isOwnedByCurrentUser) }

    private fun filteredPersonalPlans(date: LocalDate): List<AgendaItemUi> =
        personalPlans.filter { it.start.toLocalDate() == date }
}

class TimetableViewModel(
    private val timetableRepository: TimetableRepository,
    private val messagingRepository: MessagingRepository? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow(TimetableUiState(isLoading = true))
    val uiState: StateFlow<TimetableUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            runCatching { timetableRepository.getTimetable() }
                .onSuccess { response ->
                    val plannedItems = messagingRepository
                        ?.let { repo ->
                            runCatching { repo.getMessageThreads().toPlannedAgendaItems() }.getOrDefault(emptyList())
                        }
                        .orEmpty()
                    _uiState.value = response.toUiState(plannedItems)
                }
                .onFailure { throwable ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = throwable.message ?: "Could not load timetable"
                    )
                }
        }
    }

    fun toggleStudentSelection(studentId: String) {
        val current = _uiState.value.selectedStudentIds
        val next = current.toMutableSet().apply {
            if (studentId in this) {
                remove(studentId)
            } else {
                add(studentId)
            }
        }
        _uiState.value = _uiState.value.copy(selectedStudentIds = next)
    }

    fun selectAllStudents() {
        _uiState.value = _uiState.value.copy(selectedStudentIds = emptySet())
    }

    fun createPersonalPlan(
        date: LocalDate,
        startTime: LocalTime,
        endTime: LocalTime,
        title: String,
        description: String,
        planType: PersonalPlanType
    ) {
        viewModelScope.launch {
            runCatching {
                timetableRepository.createPersonalPlan(
                    CreatePersonalTimetablePlanRequestDto(
                        title = title.trim(),
                        description = description.trim().ifBlank { null },
                        date = date.toString(),
                        startTime = startTime.toString(),
                        endTime = endTime.toString(),
                        type = planType.name,
                        note = if (planType == PersonalPlanType.GROUP_WORK) {
                            // TODO: Add participant picker and invitation flow for collaborative plans.
                            "Created from mobile personal planner."
                        } else {
                            "Created from mobile personal planner."
                        }
                    )
                )
            }.onSuccess { createdPlan ->
                _uiState.value = _uiState.value.copy(
                    personalPlans = (_uiState.value.personalPlans + createdPlan.toAgendaItem()).sortedBy { it.start },
                    errorMessage = null
                )
            }.onFailure { throwable ->
                _uiState.value = _uiState.value.copy(
                    errorMessage = throwable.message ?: "Could not create your personal plan"
                )
            }
        }
    }
}

class TimetableViewModelFactory(
    private val timetableRepository: TimetableRepository,
    private val messagingRepository: MessagingRepository? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimetableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimetableViewModel(timetableRepository, messagingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

private fun MobileTimetableResponseDto.toUiState(plannedItems: List<AgendaItemUi>): TimetableUiState = TimetableUiState(
    isLoading = false,
    audience = audience,
    scopeLabel = scopeLabel,
    students = students.map {
        TimetableStudent(
            id = it.id,
            name = it.name,
            schoolId = it.schoolId,
            schoolName = it.schoolName
        )
    },
    selectedStudentIds = emptySet(),
    templates = entries.map { it.toTemplateEntry() },
    plannedItems = plannedItems,
    personalPlans = personalPlans.map { it.toAgendaItem() },
    errorMessage = null
)

private fun MobileTimetableEntryDto.toTemplateEntry(): TimetableTemplateEntry = TimetableTemplateEntry(
    id = id,
    dayOfWeek = runCatching { DayOfWeek.valueOf(dayOfWeek) }.getOrDefault(DayOfWeek.MONDAY),
    startTime = LocalTime.parse(startTime),
    endTime = LocalTime.parse(endTime),
    title = title,
    room = room.orEmpty(),
    teacher = teacher.orEmpty(),
    type = runCatching { TimetableEntryType.valueOf(type) }.getOrDefault(TimetableEntryType.LECTURE),
    studentId = studentId,
    studentName = studentName,
    note = note,
    schoolId = schoolId,
    schoolName = schoolName,
    roleContext = roleContext
)

private fun TimetableEntry.toAgendaItem(): AgendaItemUi = AgendaItemUi(
    id = buildString {
        append("class_")
        append(id)
        append("_")
        append(roleContext.lowercase(Locale.getDefault()))
        append("_")
        append(studentId ?: "shared")
    },
    start = start,
    end = end,
    title = title,
    subtitle = buildString {
        if (roleContext != "TEACHER_SELF" && teacher.isNotBlank()) append(teacher)
        if (room.isNotBlank()) {
            if (isNotEmpty()) append(" • ")
            append(room)
        }
        if (roleContext != "LINKED_STUDENT" && !studentName.isNullOrBlank()) {
            if (isNotEmpty()) append(" • ")
            append(studentName)
        }
    }.ifBlank { note ?: "School timetable" },
    badge = when (type) {
        TimetableEntryType.TEST -> "Assessment"
        TimetableEntryType.ASSEMBLY -> "Shared moment"
        TimetableEntryType.PRACTICAL -> "Practical"
        TimetableEntryType.GROUP_WORK -> "Group work"
        TimetableEntryType.REMEDIAL -> "Support"
        TimetableEntryType.LAB -> "Lab"
        TimetableEntryType.LECTURE -> "Class"
    },
    kind = if (type == TimetableEntryType.TEST) AgendaItemKind.ASSESSMENT else AgendaItemKind.CLASS,
    sourceLabel = when (roleContext) {
        "TEACHER_SELF" -> "Your schedule"
        "LINKED_STUDENT" -> studentName?.substringBefore(" ") ?: "Learner"
        "SELF_STUDENT" -> "Your classes"
        else -> "Timetable"
    },
    schoolName = schoolName,
    room = room.takeIf { it.isNotBlank() },
    studentName = studentName,
    note = note,
    statusLabel = when {
        type == TimetableEntryType.TEST -> "Prepare"
        roleContext == "TEACHER_SELF" -> "Teaching"
        else -> null
    },
    ctaLabel = if (type == TimetableEntryType.TEST) "Revision" else null,
    isImportant = type == TimetableEntryType.TEST,
    isOwnedByCurrentUser = roleContext == "TEACHER_SELF",
    origin = AgendaItemOrigin.OFFICIAL
)

private fun List<MobileMessageThreadDto>.toPlannedAgendaItems(): List<AgendaItemUi> =
    flatMap { thread -> thread.calls.mapNotNull { call -> call.toAgendaItem(thread) } }

private fun MobileThreadCallSummaryDto.toAgendaItem(thread: MobileMessageThreadDto): AgendaItemUi? {
    val start = scheduledFor?.let(::parseCallDateTime) ?: return null
    val end = start.plusMinutes(estimatedDurationMinutes())
    val kind = when {
        type.equals("LIVE_ANNOUNCEMENT", ignoreCase = true) -> AgendaItemKind.ANNOUNCEMENT
        purpose.equals("ANNOUNCEMENT", ignoreCase = true) -> AgendaItemKind.ANNOUNCEMENT
        type.equals("VIDEO", ignoreCase = true) -> AgendaItemKind.CALL
        else -> AgendaItemKind.MEETING
    }

    return AgendaItemUi(
        id = "thread_call_$id",
        start = start,
        end = end,
        title = title,
        subtitle = buildString {
            append(thread.topic)
            participantSummary?.takeIf { it.isNotBlank() }?.let {
                append(" • ")
                append(it)
            }
        },
        badge = purpose
            .lowercase()
            .replace('_', ' ')
            .split(' ')
            .joinToString(" ") { word ->
                word.replaceFirstChar { char -> char.titlecase(Locale.getDefault()) }
            },
        kind = kind,
        sourceLabel = "Thread plan",
        note = note,
        statusLabel = status.toFriendlyCallStatus(),
        ctaLabel = status.toFriendlyCallAction(),
        isImportant = status.equals("SCHEDULED", ignoreCase = true) ||
            status.equals("PENDING_CONFIRMATION", ignoreCase = true) ||
            purpose.equals("ANNOUNCEMENT", ignoreCase = true),
        origin = AgendaItemOrigin.THREAD_PLAN
    )
}

private fun MobilePersonalTimetablePlanDto.toAgendaItem(): AgendaItemUi {
    val startDate = LocalDate.parse(date)
    val start = startDate.atTime(LocalTime.parse(startTime))
    val end = startDate.atTime(LocalTime.parse(endTime))
    val participantSummary = participantLabels.joinToString(", ").takeIf { it.isNotBlank() }
    val detailNote = buildString {
        description?.takeIf { it.isNotBlank() }?.let { append(it) }
        note?.takeIf { it.isNotBlank() }?.let {
            if (isNotEmpty()) append("\n\n")
            append(it)
        }
    }.ifBlank { null }
    return AgendaItemUi(
        id = "personal_plan_$id",
        start = start,
        end = end,
        title = title,
        subtitle = buildString {
            append(type.toFriendlyPersonalPlanSubtitle())
            participantSummary?.let {
                append(" • ")
                append(it)
            }
        }.ifBlank { "Personal school plan" },
        badge = type.toFriendlyPersonalPlanBadge(),
        kind = AgendaItemKind.PERSONAL,
        sourceLabel = when (visibility.uppercase(Locale.getDefault())) {
            "SHARED" -> "Shared plan"
            else -> "Your plan"
        },
        note = detailNote,
        statusLabel = when (visibility.uppercase(Locale.getDefault())) {
            "SHARED" -> "Shared"
            else -> "Private"
        },
        ctaLabel = if (participantLabels.isNotEmpty()) "Group plan" else null,
        isImportant = type.equals("PROJECT_MILESTONE", ignoreCase = true) ||
            type.equals("GROUP_WORK", ignoreCase = true),
        isOwnedByCurrentUser = true,
        origin = AgendaItemOrigin.PERSONAL_PLAN
    )
}

private fun parseCallDateTime(raw: String): LocalDateTime? = runCatching {
    OffsetDateTime.parse(raw).toLocalDateTime()
}.getOrNull()

private fun MobileThreadCallSummaryDto.estimatedDurationMinutes(): Long {
    durationLabel
        ?.substringBefore(' ')
        ?.toLongOrNull()
        ?.let { return it }
    return when {
        purpose.equals("ANNOUNCEMENT", ignoreCase = true) -> 45L
        type.equals("VIDEO", ignoreCase = true) -> 30L
        else -> 20L
    }
}

private fun String.toFriendlyCallStatus(): String = when {
    equals("PENDING_CONFIRMATION", ignoreCase = true) -> "Awaiting confirmation"
    equals("SCHEDULED", ignoreCase = true) -> "Scheduled"
    equals("LIVE", ignoreCase = true) || equals("ONGOING", ignoreCase = true) -> "Live now"
    equals("COMPLETED", ignoreCase = true) -> "Completed"
    equals("CANCELLED", ignoreCase = true) -> "Cancelled"
    else -> lowercase().replace('_', ' ').replaceFirstChar { it.titlecase(Locale.getDefault()) }
}

private fun String.toFriendlyCallAction(): String? = when {
    equals("PENDING_CONFIRMATION", ignoreCase = true) -> "Respond"
    equals("SCHEDULED", ignoreCase = true) -> "Join soon"
    equals("LIVE", ignoreCase = true) || equals("ONGOING", ignoreCase = true) -> "Join now"
    else -> null
}

private fun String.toFriendlyPersonalPlanBadge(): String = when {
    equals("STUDY_BLOCK", ignoreCase = true) -> "Study block"
    equals("HOMEWORK", ignoreCase = true) -> "Homework"
    equals("GROUP_WORK", ignoreCase = true) -> "Group work"
    equals("PROJECT_MILESTONE", ignoreCase = true) -> "Project"
    equals("CLUB_ACTIVITY", ignoreCase = true) -> "Club"
    equals("MEETING", ignoreCase = true) -> "Personal meeting"
    else -> "Reminder"
}

private fun String.toFriendlyPersonalPlanSubtitle(): String = when {
    equals("STUDY_BLOCK", ignoreCase = true) -> "Time reserved for focused study"
    equals("HOMEWORK", ignoreCase = true) -> "Personal homework session"
    equals("GROUP_WORK", ignoreCase = true) -> "Collaborative school work"
    equals("PROJECT_MILESTONE", ignoreCase = true) -> "Project checkpoint"
    equals("CLUB_ACTIVITY", ignoreCase = true) -> "School activity or club plan"
    equals("MEETING", ignoreCase = true) -> "Personal meeting slot"
    else -> "Personal school reminder"
}

fun LocalDate.shortWeekdayLabel(): String =
    dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
