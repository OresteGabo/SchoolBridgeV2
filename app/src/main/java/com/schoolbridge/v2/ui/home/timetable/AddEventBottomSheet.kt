package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AddEventBottomSheet(
    selectedDate: LocalDate,
    audienceSummary: String?,
    participantOptions: List<TimetableParticipantOption>,
    defaultParticipantIds: Set<Long>,
    existingPlan: AgendaItemUi? = null,
    existingAgenda: List<AgendaItemUi>,
    isSaving: Boolean,
    onDismiss: () -> Unit,
    onAddEvent: (LocalTime, LocalTime, String, String, PersonalPlanType, PlanVisibility, List<Long>, Int?) -> Unit,
    onDeletePlan: (() -> Unit)? = null
) {
    val isEditing = existingPlan != null
    val initialType = remember(existingPlan?.id) {
        existingPlan?.badge?.toPersonalPlanTypeOrDefault() ?: PersonalPlanType.STUDY_BLOCK
    }
    val initialVisibility = remember(existingPlan?.id) {
        if (existingPlan?.sourceLabel?.contains("Shared", ignoreCase = true) == true) {
            PlanVisibility.SHARED
        } else {
            PlanVisibility.PRIVATE
        }
    }
    var startTime by remember(existingPlan?.id) { mutableStateOf(existingPlan?.start?.toLocalTime() ?: LocalTime.of(8, 0)) }
    var endTime by remember(existingPlan?.id) { mutableStateOf(existingPlan?.end?.toLocalTime() ?: LocalTime.of(9, 0)) }
    var title by remember(existingPlan?.id) { mutableStateOf(existingPlan?.title.orEmpty()) }
    var description by remember(existingPlan?.id) { mutableStateOf(existingPlan?.note?.substringBefore("\n\nCreated from mobile personal planner.").orEmpty()) }
    var selectedType by remember(existingPlan?.id) { mutableStateOf(initialType) }
    var selectedVisibility by remember(existingPlan?.id) { mutableStateOf(initialVisibility) }
    var selectedParticipantIds by remember(existingPlan?.id, defaultParticipantIds) {
        mutableStateOf(existingPlan?.participantUserIds?.toSet()?.takeIf { it.isNotEmpty() } ?: defaultParticipantIds)
    }
    var selectedReminderMinutes by remember(existingPlan?.id) { mutableStateOf(existingPlan?.reminderMinutesBefore) }
    var showTypeMenu by remember { mutableStateOf(false) }
    var showReminderMenu by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val suggestedDurations = remember {
        listOf(30L to "30 min", 60L to "1 hour", 120L to "2 hours")
    }
    val reminderOptions = remember {
        listOf(
            null to "No reminder",
            5 to "5 minutes before",
            15 to "15 minutes before",
            30 to "30 minutes before",
            60 to "1 hour before"
        )
    }
    val overlappingItems = remember(selectedDate, startTime, endTime, existingAgenda, existingPlan?.id) {
        existingAgenda.filter { agendaItem ->
            if (agendaItem.id == existingPlan?.id) return@filter false
            val startsInside = !agendaItem.start.toLocalTime().isBefore(startTime) && agendaItem.start.toLocalTime().isBefore(endTime)
            val endsInside = agendaItem.end.toLocalTime().isAfter(startTime) && !agendaItem.end.toLocalTime().isAfter(endTime)
            val wrapsSelection = agendaItem.start.toLocalTime().isBefore(endTime) && agendaItem.end.toLocalTime().isAfter(startTime)
            startsInside || endsInside || wrapsSelection
        }
    }

    val formattedDate = remember(selectedDate) {
        val day = selectedDate.dayOfMonth
        val suffix = when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
        val month = selectedDate.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        "$day$suffix $month"
    }

    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = if (isEditing) "Update plan on $formattedDate" else "Plan something on $formattedDate",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.Top) {
            Icon(Icons.Default.Info, contentDescription = "Info", modifier = Modifier.size(16.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Use this for school planning you control, like study blocks, homework sessions, project checkpoints, or group work. Official timetable entries still come from the school.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { if (it.length <= 60) title = it },
            label = { Text("Plan title") },
            placeholder = { Text(selectedType.defaultTitle()) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { if (it.length <= 180) description = it },
            label = { Text("Note or purpose (optional)") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
            maxLines = 4
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Plan type",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(8.dp))
        Box {
            OutlinedButton(
                onClick = { showTypeMenu = true },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedType.toLabel())
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Open plan type list"
                    )
                }
            }
            DropdownMenu(
                expanded = showTypeMenu,
                onDismissRequest = { showTypeMenu = false }
            ) {
                PersonalPlanType.entries.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.toLabel()) },
                        onClick = {
                            selectedType = type
                            showTypeMenu = false
                        },
                        trailingIcon = if (type == selectedType) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Visibility",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VisibilityChip(
                label = "Private",
                description = "Only in your own planner",
                selected = selectedVisibility == PlanVisibility.PRIVATE,
                onClick = { selectedVisibility = PlanVisibility.PRIVATE },
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            )
            VisibilityChip(
                label = "Shared",
                description = audienceSummary ?: "Use for collaborative school planning",
                selected = selectedVisibility == PlanVisibility.SHARED,
                onClick = { selectedVisibility = PlanVisibility.SHARED },
                enabled = !isSaving,
                modifier = Modifier.weight(1f)
            )
        }

        if (selectedVisibility == PlanVisibility.SHARED) {
            Spacer(Modifier.height(12.dp))

            Text(
                text = "Who should see this?",
                style = MaterialTheme.typography.labelLarge
            )
            Spacer(Modifier.height(8.dp))

            if (participantOptions.isEmpty()) {
                Surface(
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f))
                ) {
                    Text(
                        text = "No linked people are available yet in this timetable view. You can still save this now and invite others later when the school directory is connected here.",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                ParticipantSelectionBlock(
                    options = participantOptions,
                    selectedIds = selectedParticipantIds,
                    enabled = !isSaving,
                    onToggle = { participantId ->
                        selectedParticipantIds = selectedParticipantIds.toMutableSet().apply {
                            if (participantId in this) remove(participantId) else add(participantId)
                        }
                    }
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Reminder",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(8.dp))
        Box {
            OutlinedButton(
                onClick = { showReminderMenu = true },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(selectedReminderMinutes.toReminderLabel())
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Open reminder list"
                    )
                }
            }
            DropdownMenu(
                expanded = showReminderMenu,
                onDismissRequest = { showReminderMenu = false }
            ) {
                reminderOptions.forEach { (minutes, label) ->
                    DropdownMenuItem(
                        text = { Text(label) },
                        onClick = {
                            selectedReminderMinutes = minutes
                            showReminderMenu = false
                        },
                        trailingIcon = if (minutes == selectedReminderMinutes) {
                            {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null
                                )
                            }
                        } else {
                            null
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Time",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(8.dp))
        WheelTimePicker(
            startTime = startTime,
            endTime = endTime,
            enabled = !isSaving,
            onStartTimeChange = { startTime = it },
            onEndTimeChange = { endTime = it }
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Quick duration",
            style = MaterialTheme.typography.labelLarge
        )
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestedDurations.forEach { (minutes, label) ->
                FilterChip(
                    selected = endTime == startTime.plusMinutes(minutes),
                    onClick = { endTime = startTime.plusMinutes(minutes) },
                    enabled = !isSaving,
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors()
                )
            }
        }

        if (overlappingItems.isNotEmpty()) {
            Spacer(Modifier.height(14.dp))
            ScheduleConflictNotice(items = overlappingItems)
        }

        Spacer(Modifier.height(24.dp))

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextButton(onClick = onDismiss, enabled = !isSaving) { Text("Cancel") }
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    onAddEvent(
                        startTime,
                        endTime,
                        title.ifBlank { selectedType.defaultTitle() },
                        description,
                        selectedType,
                        selectedVisibility,
                        if (selectedVisibility == PlanVisibility.SHARED) {
                            selectedParticipantIds.toList()
                        } else {
                            emptyList()
                        },
                        selectedReminderMinutes
                    )
                },
                enabled = startTime < endTime && !isSaving
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(if (isEditing) "Updating..." else "Saving...")
                } else {
                    Text(if (isEditing) "Update plan" else "Save plan")
                }
            }
        }

        if (isEditing && onDeletePlan != null) {
            Spacer(Modifier.height(12.dp))
            OutlinedButton(
                onClick = onDeletePlan,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.48f))
            ) {
                Text("Delete this plan")
            }
        }
    }
}

@Composable
private fun ParticipantSelectionBlock(
    options: List<TimetableParticipantOption>,
    selectedIds: Set<Long>,
    enabled: Boolean,
    onToggle: (Long) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            val selected = option.userId in selectedIds
            Surface(
                onClick = { onToggle(option.userId) },
                enabled = enabled,
                shape = MaterialTheme.shapes.large,
                color = if (selected) {
                    MaterialTheme.colorScheme.tertiaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerLow
                },
                border = BorderStroke(
                    1.dp,
                    if (selected) MaterialTheme.colorScheme.tertiary
                    else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = option.name,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (selected) {
                                MaterialTheme.colorScheme.onTertiaryContainer
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                        option.schoolName?.let { schoolName ->
                            Text(
                                text = schoolName,
                                style = MaterialTheme.typography.bodySmall,
                                color = if (selected) {
                                    MaterialTheme.colorScheme.onTertiaryContainer.copy(alpha = 0.82f)
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                    }
                    if (selected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ScheduleConflictNotice(items: List<AgendaItemUi>) {
    val formatter = remember { DateTimeFormatter.ofPattern("HH:mm") }
    Surface(
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.72f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.28f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = if (items.size == 1) {
                    "This overlaps another school moment"
                } else {
                    "This overlaps ${items.size} school moments"
                },
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            items.take(3).forEach { item ->
                Text(
                    text = "${item.start.format(formatter)}-${item.end.format(formatter)} • ${item.title}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.88f)
                )
            }
            if (items.size > 3) {
                Text(
                    text = "+${items.size - 3} more",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.82f)
                )
            }
        }
    }
}

@Composable
private fun VisibilityChip(
    label: String,
    description: String,
    selected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = MaterialTheme.shapes.large,
        color = if (selected) {
            MaterialTheme.colorScheme.secondaryContainer
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = if (selected) {
                MaterialTheme.colorScheme.secondary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }
        ),
        enabled = enabled
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (selected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = if (selected) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) {
                        MaterialTheme.colorScheme.onSecondaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = if (selected) {
                    MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.82f)
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}



@Composable
private fun WheelTimePicker(
    startTime: LocalTime,
    endTime: LocalTime,
    enabled: Boolean,
    onStartTimeChange: (LocalTime) -> Unit,
    onEndTimeChange: (LocalTime) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WheelTimeSelector(
            label = "Start",
            time = startTime,
            enabled = enabled,
            onTimeSelected = onStartTimeChange,
            modifier = Modifier.weight(1f)
        )
        WheelTimeSelector(
            label = "End",
            time = endTime,
            enabled = enabled,
            onTimeSelected = onEndTimeChange,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun WheelTimeSelector(
    label: String,
    time: LocalTime,
    enabled: Boolean,
    onTimeSelected: (LocalTime) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimeWheelColumn(
                    values = (0..23).toList(),
                    selectedValue = time.hour,
                    format = { it.toString().padStart(2, '0') },
                    onSelected = { onTimeSelected(LocalTime.of(it, time.minute)) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TimeWheelColumn(
                    values = listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55),
                    selectedValue = nearestMinuteStep(time.minute),
                    format = { it.toString().padStart(2, '0') },
                    onSelected = { onTimeSelected(LocalTime.of(time.hour, it)) },
                    enabled = enabled,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = time.format(DateTimeFormatter.ofPattern("HH:mm")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun TimeWheelColumn(
    values: List<Int>,
    selectedValue: Int,
    format: (Int) -> String,
    onSelected: (Int) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    val itemHeight = 44.dp
    val visibleRows = 3
    val scope = rememberCoroutineScope()
    val selectedIndex = values.indexOf(selectedValue).coerceAtLeast(0)
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(selectedValue) {
        val targetIndex = values.indexOf(selectedValue).coerceAtLeast(0)
        if (listState.firstVisibleItemIndex != targetIndex) {
            listState.scrollToItem(targetIndex)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val resolvedIndex = (listState.firstVisibleItemIndex +
                if (listState.firstVisibleItemScrollOffset > 22) 1 else 0)
                .coerceIn(0, values.lastIndex)
            scope.launch {
                listState.animateScrollToItem(resolvedIndex)
            }
            onSelected(values[resolvedIndex])
        }
    }

    Box(
        modifier = modifier
            .height(88.dp)
            .pointerInput(Unit) {
                detectVerticalDragGestures { change, _ ->
                    if (enabled) {
                        change.consume()
                    }
                }
            }
    ) {
        Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .height(itemHeight),
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f)
        ) {}

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth(),
            flingBehavior = flingBehavior,
            userScrollEnabled = enabled,
            contentPadding = PaddingValues(vertical = 22.dp)
        ) {
            itemsIndexed(values) { index, value ->
                val isSelected = index == selectedIndex && !listState.isScrollInProgress
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = format(value),
                        style = if (isSelected) {
                            MaterialTheme.typography.titleMedium
                        } else {
                            MaterialTheme.typography.bodyLarge
                        },
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onSecondaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.56f)
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

private fun nearestMinuteStep(minute: Int): Int =
    listOf(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55)
        .minByOrNull { kotlin.math.abs(it - minute) }
        ?: 0

private fun PersonalPlanType.toLabel(): String = when (this) {
    PersonalPlanType.STUDY_BLOCK -> "Study block"
    PersonalPlanType.HOMEWORK -> "Homework"
    PersonalPlanType.REVISION -> "Revision"
    PersonalPlanType.EXAM_PREP -> "Exam prep"
    PersonalPlanType.GROUP_WORK -> "Group work"
    PersonalPlanType.PROJECT_MILESTONE -> "Project milestone"
    PersonalPlanType.CLUB_ACTIVITY -> "Club activity"
    PersonalPlanType.MEETING -> "Meeting"
    PersonalPlanType.PARENT_FOLLOW_UP -> "Parent follow-up"
    PersonalPlanType.VERIFICATION_APPOINTMENT -> "Verification appointment"
    PersonalPlanType.REMINDER -> "Reminder"
}

private fun PersonalPlanType.defaultTitle(): String = when (this) {
    PersonalPlanType.STUDY_BLOCK -> "Study session"
    PersonalPlanType.HOMEWORK -> "Homework time"
    PersonalPlanType.REVISION -> "Revision session"
    PersonalPlanType.EXAM_PREP -> "Exam preparation"
    PersonalPlanType.GROUP_WORK -> "Group work session"
    PersonalPlanType.PROJECT_MILESTONE -> "Project checkpoint"
    PersonalPlanType.CLUB_ACTIVITY -> "Club activity"
    PersonalPlanType.MEETING -> "School meeting"
    PersonalPlanType.PARENT_FOLLOW_UP -> "Parent follow-up"
    PersonalPlanType.VERIFICATION_APPOINTMENT -> "Verification appointment"
    PersonalPlanType.REMINDER -> "School reminder"
}

private fun String.toPersonalPlanTypeOrDefault(): PersonalPlanType = when {
    equals("Study block", ignoreCase = true) -> PersonalPlanType.STUDY_BLOCK
    equals("Homework", ignoreCase = true) -> PersonalPlanType.HOMEWORK
    equals("Revision", ignoreCase = true) -> PersonalPlanType.REVISION
    equals("Exam prep", ignoreCase = true) -> PersonalPlanType.EXAM_PREP
    equals("Group work", ignoreCase = true) -> PersonalPlanType.GROUP_WORK
    equals("Project", ignoreCase = true) -> PersonalPlanType.PROJECT_MILESTONE
    equals("Club", ignoreCase = true) -> PersonalPlanType.CLUB_ACTIVITY
    equals("Personal meeting", ignoreCase = true) -> PersonalPlanType.MEETING
    equals("Follow-up", ignoreCase = true) -> PersonalPlanType.PARENT_FOLLOW_UP
    equals("Verification", ignoreCase = true) -> PersonalPlanType.VERIFICATION_APPOINTMENT
    else -> PersonalPlanType.REMINDER
}

private fun Int?.toReminderLabel(): String = when (this) {
    null -> "No reminder"
    5 -> "5 minutes before"
    15 -> "15 minutes before"
    30 -> "30 minutes before"
    60 -> "1 hour before"
    else -> "$this minutes before"
}
