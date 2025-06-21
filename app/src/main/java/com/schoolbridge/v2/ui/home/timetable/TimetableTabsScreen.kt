package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.academic.TimetableEntry
import com.schoolbridge.v2.domain.academic.timetableEntryColor
import java.time.LocalDate
import java.time.LocalTime
import java.time.Duration
import java.time.DayOfWeek
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.ui.Modifier
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimetableTabsScreen(
    onBack: () -> Unit
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Weekly", "Daily")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timetable") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                when (selectedTabIndex) {
                    0 -> WeeklyTimetableTab(
                        events = sampleEvents,
                        onBack = {} // Pass your back logic if needed here
                    )
                    1 -> DailyTimetableTab()
                }
            }
        }
    )
}

// Constants for timetable layout
val MINUTE_HEIGHT_DP = 1.2.dp
val TIMELINE_START_HOUR = 7
val TIMELINE_END_HOUR = 18 // Inclusive, so 18:00 is the last label
val HOUR_HEIGHT_DP = MINUTE_HEIGHT_DP * 60f

@Composable
fun DailyTimetableTab() {
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val visibleDates = remember { (0..14).map { LocalDate.now().plusDays(it.toLong()) } }
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    Column(Modifier.fillMaxSize()) {
        // Date selector (horizontal scroll)
        Row(
            Modifier
                .horizontalScroll(horizontalScrollState)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            visibleDates.forEach { date ->
                val isSelected = selectedDate == date
                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clickable { selectedDate = date },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        val dailyEvents = sampleEvents
            .filter { it.day == selectedDate.dayOfWeek }
            .sortedBy { it.start }

        // Main timetable content area: timeline labels + events
        Row(
            Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
        ) {
            val totalHours = (TIMELINE_END_HOUR - TIMELINE_START_HOUR) + 1 // e.g., 7-18 -> 12 hours
            val timelineHeightInDp = HOUR_HEIGHT_DP * totalHours

            // Timeline labels and the vertical line
            Box(
                modifier = Modifier
                    .width(IntrinsicSize.Min) // Allows content to dictate width
                    .padding(start = 8.dp, end = 4.dp)
                    .height(timelineHeightInDp + HOUR_HEIGHT_DP / 2) // Total height to accommodate dots at top/bottom edges
            ) {
                // Vertical Line (behind time labels)
                val lineColor = MaterialTheme.colorScheme.outlineVariant
                Canvas(
                    modifier = Modifier
                        .fillMaxHeight() // Fill the height of this Box (which is the timelineHeightInDp)
                        .width(2.dp) // Width of the line
                        .align(Alignment.CenterEnd) // Align to the right of this Box
                ) {
                    val canvasHeight = size.height
                    val centerX = size.width / 2f
                    drawLine(
                        color = lineColor,
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, canvasHeight),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }

                // Time labels
                Column(
                    modifier = Modifier
                        .fillMaxHeight() // Fill the height of the parent Box
                        .align(Alignment.CenterEnd) // Align to the right, on top of the line
                        .padding(end = 4.dp) // Padding for the labels
                ) {
                    // Spacer to align the first hour label's center with the top of the timeline
                    // This shifts the labels down so 07:00 is centered at the first dot.
                    Spacer(modifier = Modifier.height(HOUR_HEIGHT_DP / 2))

                    (TIMELINE_START_HOUR..TIMELINE_END_HOUR).forEach { hour ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth() // Take full width of the column
                                .height(HOUR_HEIGHT_DP), // Height of each hour slot
                            contentAlignment = Alignment.CenterEnd // Align text to the end
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "%02d:00".format(hour),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.weight(1f) // Push text to left
                                )

                                // The dot exactly centered on the vertical line
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                        .padding(horizontal = 4.dp) // Add some padding to push it right of the text
                                )
                            }
                        }
                    }
                }
            }


            // Divider between timeline and events (optional, if you want a clear visual separation)
            Divider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            // Events area with fixed height and offset calculations
            Box(
                modifier = Modifier
                    .weight(1f) // Takes remaining width
                    .padding(horizontal = 12.dp)
                    .height(timelineHeightInDp + HOUR_HEIGHT_DP / 2) // Match the height of the timeline labels
            ) {
                val minTime = LocalTime.of(TIMELINE_START_HOUR, 0)
                val maxTime = LocalTime.of(TIMELINE_END_HOUR + 1, 0) // Up to the start of the next hour
                val totalMinutesInTimeline = Duration.between(minTime, maxTime).toMinutes()

                val dpPerMinute = if (totalMinutesInTimeline > 0) {
                    (timelineHeightInDp.value + (HOUR_HEIGHT_DP.value / 2)) / totalMinutesInTimeline.toFloat() // Correct dp/minute calculation based on full height
                } else {
                    0f
                }.dp // Convert back to Dp

                if (dailyEvents.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No events for this day.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    dailyEvents.forEach { entry ->
                        val startMinutesSinceMinTime = Duration.between(minTime, entry.start).toMinutes()
                        val eventDurationMinutes = Duration.between(entry.start, entry.end).toMinutes()

                        val eventOffsetY = dpPerMinute * startMinutesSinceMinTime.toFloat()
                        val eventHeight = dpPerMinute * eventDurationMinutes.toFloat()

                        DailyCourseCard(
                            entry = entry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = eventOffsetY + (HOUR_HEIGHT_DP / 2)) // Adjust offset to align with the correct time, considering the top spacer
                                .height(eventHeight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DailyCourseCard(
    entry: TimetableEntry,
    modifier: Modifier = Modifier
) {
    val eventColor = timetableEntryColor(entry.type)
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(eventColor)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = entry.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = "${entry.start.format(timeFormatter)} – ${entry.end.format(timeFormatter)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (entry.teacher.isNotBlank() || entry.room.isNotBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (entry.teacher.isNotBlank()) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                entry.teacher,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.width(8.dp))
                        }
                        if (entry.room.isNotBlank()) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.secondary
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                entry.room,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

/*
// Placeholder for WeeklyTimetableTab - you'd have your actual implementation here
@Composable
fun WeeklyTimetableTab(events: List<TimetableEntry>, onBack: () -> Unit) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Weekly Timetable Content Goes Here")
    }
}
*/