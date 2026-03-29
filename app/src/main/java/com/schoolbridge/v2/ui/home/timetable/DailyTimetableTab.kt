package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DailyTimetableTab(
    agendaItems: List<AgendaItemUi>,
    nextDateWithEvent: LocalDate?,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    wideLandscape: Boolean,
    density: AgendaDensity,
    emptyTitle: String,
    emptyMessage: String,
    onAgendaItemActionClick: (AgendaItemUi) -> Unit = {},
    onAgendaItemClick: (AgendaItemUi) -> Unit = {}
) {
    if (wideLandscape) {
        WideDailyTimetableTab(
            agendaItems = agendaItems,
            nextDateWithEvent = nextDateWithEvent,
            selectedDate = selectedDate,
            onDateChange = onDateChange,
            density = density,
            emptyTitle = emptyTitle,
            emptyMessage = emptyMessage,
            onAgendaItemActionClick = onAgendaItemActionClick,
            onAgendaItemClick = onAgendaItemClick
        )
    } else {
        Column(Modifier.fillMaxSize()) {
            AgendaDateRibbon(
                selectedDate = selectedDate,
                onDateSelected = onDateChange
            )

            Spacer(Modifier.height(14.dp))

            AgendaDayContent(
                agendaItems = agendaItems,
                nextDateWithEvent = nextDateWithEvent,
                selectedDate = selectedDate,
                onDateChange = onDateChange,
                density = density,
                emptyTitle = emptyTitle,
                emptyMessage = emptyMessage,
                onAgendaItemActionClick = onAgendaItemActionClick,
                onAgendaItemClick = onAgendaItemClick
            )
        }
    }
}

@Composable
private fun WideDailyTimetableTab(
    agendaItems: List<AgendaItemUi>,
    nextDateWithEvent: LocalDate?,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    density: AgendaDensity,
    emptyTitle: String,
    emptyMessage: String,
    onAgendaItemActionClick: (AgendaItemUi) -> Unit,
    onAgendaItemClick: (AgendaItemUi) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        AgendaDateColumn(
            selectedDate = selectedDate,
            onDateSelected = onDateChange,
            modifier = Modifier
                .width(132.dp)
                .fillMaxHeight()
        )
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f),
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.42f)
            ),
            tonalElevation = 1.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = selectedDate.format(java.time.format.DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp)
                )
                AgendaDayContent(
                    agendaItems = agendaItems,
                    nextDateWithEvent = nextDateWithEvent,
                    selectedDate = selectedDate,
                    onDateChange = onDateChange,
                    density = density,
                    emptyTitle = emptyTitle,
                    emptyMessage = emptyMessage,
                    onAgendaItemActionClick = onAgendaItemActionClick,
                    onAgendaItemClick = onAgendaItemClick
                )
            }
        }
        WideDayDigestRail(
            agendaItems = agendaItems,
            nextDateWithEvent = nextDateWithEvent,
            onDateChange = onDateChange,
            modifier = Modifier
                .width(274.dp)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun AgendaDayContent(
    agendaItems: List<AgendaItemUi>,
    nextDateWithEvent: LocalDate?,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    density: AgendaDensity,
    emptyTitle: String,
    emptyMessage: String,
    onAgendaItemActionClick: (AgendaItemUi) -> Unit,
    onAgendaItemClick: (AgendaItemUi) -> Unit
) {
    if (agendaItems.isEmpty()) {
        NoEventsPlaceholder(
            selectedDate = selectedDate,
            nextDateWithEvent = nextDateWithEvent,
            onDateChange = onDateChange,
            title = emptyTitle,
            message = emptyMessage
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(agendaItems, key = { it.id }) { item ->
                AgendaCard(
                    item = item,
                    density = density,
                    onCTAClick = { onAgendaItemActionClick(item) },
                    onCardClick = { onAgendaItemClick(item) }
                )
            }
        }
    }
}

@Composable
private fun WideDayDigestRail(
    agendaItems: List<AgendaItemUi>,
    nextDateWithEvent: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val firstStart = agendaItems.minByOrNull { it.start }?.start?.toLocalTime()
    val lastEnd = agendaItems.maxByOrNull { it.end }?.end?.toLocalTime()
    val kindCounts = remember(agendaItems) {
        AgendaItemKind.entries.mapNotNull { kind ->
            val count = agendaItems.count { it.kind == kind }
            if (count > 0) kind to count else null
        }.sortedByDescending { it.second }
    }
    val importantCount = remember(agendaItems) { agendaItems.count { it.isImportant } }
    val meetingCount = remember(agendaItems) {
        agendaItems.count { it.kind == AgendaItemKind.MEETING || it.kind == AgendaItemKind.CALL }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WideDigestCard(
            title = "Day pulse",
            body = if (agendaItems.isEmpty()) {
                "This day is still open."
            } else {
                buildString {
                    append("${agendaItems.size} planned ")
                    append(if (agendaItems.size == 1) "moment" else "moments")
                    firstStart?.let { start ->
                        append(" from ${start.asClockText()}")
                    }
                    lastEnd?.let { end ->
                        append(" to ${end.asClockText()}")
                    }
                }
            }
        ) {
            if (importantCount > 0) {
                Text(
                    text = "$importantCount marked important",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (meetingCount > 0) {
                Text(
                    text = "$meetingCount meeting or call ${if (meetingCount == 1) "slot" else "slots"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        WideDigestCard(
            title = "What fills the day",
            body = if (kindCounts.isEmpty()) {
                "Classes, meetings, plans, and school notices will appear here when they are scheduled."
            } else {
                "A quick split of how this day is being used."
            }
        ) {
            if (kindCounts.isEmpty()) {
                nextDateWithEvent?.let { nextDate ->
                    TextButton(onClick = { onDateChange(nextDate) }) {
                        Text("Jump to next scheduled day")
                    }
                }
            } else {
                kindCounts.take(4).forEach { (kind, count) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = kind.toDigestLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (agendaItems.isNotEmpty()) {
            val ownedPlans = agendaItems.count { it.origin == AgendaItemOrigin.PERSONAL_PLAN }
            val sharedCourses = agendaItems.count { it.linkedStudentNames.size > 1 }
            WideDigestCard(
                title = "Context",
                body = "Useful signals to scan the day before opening every card."
            ) {
                if (ownedPlans > 0) {
                    Text(
                        text = "$ownedPlans personal ${if (ownedPlans == 1) "plan" else "plans"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (sharedCourses > 0) {
                    Text(
                        text = "$sharedCourses shared family ${if (sharedCourses == 1) "moment" else "moments"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (ownedPlans == 0 && sharedCourses == 0) {
                    Text(
                        text = "This day is mostly made of direct school timetable entries.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun WideDigestCard(
    title: String,
    body: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.88f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            content = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                content()
            }
        )
    }
}

private fun AgendaItemKind.toDigestLabel(): String = when (this) {
    AgendaItemKind.CLASS -> "Classes"
    AgendaItemKind.ASSESSMENT -> "Assessments"
    AgendaItemKind.MEETING -> "Meetings"
    AgendaItemKind.CALL -> "Calls"
    AgendaItemKind.ANNOUNCEMENT -> "Announcements"
    AgendaItemKind.PERSONAL -> "Personal plans"
}

private fun LocalTime.asClockText(): String = format(DateTimeFormatter.ofPattern("HH:mm"))

@Composable
private fun AgendaDateRibbon(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val month = YearMonth.from(selectedDate)
    val days = (1..month.lengthOfMonth()).map { month.atDay(it) }
    val listState = rememberLazyListState()

    LaunchedEffect(days, selectedDate) {
        val index = days.indexOf(selectedDate)
        if (index >= 0) {
            val targetIndex = (index - 1).coerceAtLeast(0)
            listState.animateScrollToItem(targetIndex)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.year,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(Modifier.height(8.dp))

        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(days) { date ->
                val isSelected = date == selectedDate
                val containerColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerLow
                    },
                    label = "date_container"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    label = "date_content"
                )
                val elevation by animateDpAsState(
                    targetValue = if (isSelected) 10.dp else 0.dp,
                    label = "date_elevation"
                )
                val scale by animateDpAsState(
                    targetValue = if (isSelected) 1.dp else 0.dp,
                    label = "date_scale"
                )

                Card(
                    onClick = { onDateSelected(date) },
                    colors = CardDefaults.cardColors(containerColor = containerColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = elevation),
                    shape = MaterialTheme.shapes.large,
                    modifier = Modifier.scale(1f + scale.value * 0.03f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = date.shortWeekdayLabel(),
                            style = MaterialTheme.typography.labelMedium,
                            color = contentColor
                        )
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendaDateColumn(
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val month = YearMonth.from(selectedDate)
    val days = remember(month) { (1..month.lengthOfMonth()).map { month.atDay(it) } }
    val listState = rememberLazyListState()

    LaunchedEffect(days, selectedDate) {
        val index = days.indexOf(selectedDate)
        if (index >= 0) {
            listState.animateScrollToItem((index - 2).coerceAtLeast(0))
        }
    }

    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.extraLarge,
        color = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.92f),
        border = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.45f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = month.month.getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + month.year,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(start = 10.dp, end = 10.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(days) { date ->
                    val isSelected = date == selectedDate
                    val containerColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                        label = "column_date_container"
                    )
                    val contentColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        label = "column_date_content"
                    )
                    Card(
                        onClick = { onDateSelected(date) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 8.dp else 0.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 12.dp)
                        ) {
                            Text(
                                text = date.shortWeekdayLabel(),
                                style = MaterialTheme.typography.labelMedium,
                                color = contentColor
                            )
                            Text(
                                text = date.dayOfMonth.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = contentColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoEventsPlaceholder(
    selectedDate: LocalDate,
    nextDateWithEvent: LocalDate?,
    onDateChange: (LocalDate) -> Unit,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    val dayName = selectedDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    val fallbackTitle = "A quiet schedule for $dayName"

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth(0.86f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(1.dp)
                        .padding(end = 0.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.EventBusy,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .size(18.dp)
                )
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(1.dp)
                ) {
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                    )
                }
            }

            Text(
                text = dayName.uppercase(Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.outline,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(top = 18.dp)
            )

            Text(
                text = title.ifBlank {
                    fallbackTitle
                },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 14.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier.padding(top = 12.dp)
            )

            nextDateWithEvent?.let {
                Row(
                    modifier = Modifier.padding(top = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Next scheduled day: ${it.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            if (nextDateWithEvent != null) {
                TextButton(onClick = { onDateChange(nextDateWithEvent) }) {
                    Text(
                        text = "Jump to it"
                    )
                }
            }
        }
    }
}
