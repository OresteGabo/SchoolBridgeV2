package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

// Constants for timetable layout
val MINUTE_HEIGHT_DP = 1.2.dp
const val TIMELINE_START_HOUR = 7
const val TIMELINE_END_HOUR = 18 // Inclusive, so 18:00 is the last label
val HOUR_HEIGHT_DP = MINUTE_HEIGHT_DP * 60f

@Composable
fun DailyTimetableTab(
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    // --- UI state ----------------------------------------------------------
    val visibleDates = remember { (0..14).map { LocalDate.now().plusDays(it.toLong()) } }
    val horizontalScrollState = rememberScrollState()
    val verticalScrollState = rememberScrollState()

    // --- Layout ------------------------------------------------------------
    Column(Modifier.fillMaxSize()) {

        /** ─── Date selector ─────────────────────────────────────────────── */
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
                        .clickable { onDateChange(date) },
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
                                color = if (isSelected)
                                    MaterialTheme.colorScheme.primary
                                else Color.Transparent,
                                shape = MaterialTheme.shapes.medium
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (isSelected) Color.White
                            else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        /** ─── Events filtered for this day ──────────────────────────────── */
        val dailyEvents = sampleEvents
            .filter { it.day == selectedDate.dayOfWeek }
            .sortedBy { it.start }

        /** ─── Timeline + events area ───────────────────────────────────── */
        Row(
            Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
        ) {
            val totalHours = (TIMELINE_END_HOUR - TIMELINE_START_HOUR) + 1
            val timelineHeightInDp = HOUR_HEIGHT_DP * totalHours

            /* Timeline labels + vertical line */
            Box(
                modifier = Modifier
                    .width(IntrinsicSize.Min)
                    .padding(start = 8.dp, end = 4.dp)
                    .height(timelineHeightInDp + HOUR_HEIGHT_DP / 2)
            ) {
                val lineColor = MaterialTheme.colorScheme.outlineVariant
                Canvas(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(2.dp)
                        .align(Alignment.CenterEnd)
                ) {
                    val centerX = size.width / 2f
                    drawLine(
                        color = lineColor,
                        start = Offset(centerX, 0f),
                        end = Offset(centerX, size.height),
                        strokeWidth = 4f,
                        cap = StrokeCap.Round
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .align(Alignment.CenterEnd)
                        .offset(x = 4.dp)
                ) {
                    Spacer(Modifier.height(HOUR_HEIGHT_DP / 2))
                    (TIMELINE_START_HOUR..TIMELINE_END_HOUR).forEach { hour ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(HOUR_HEIGHT_DP),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "%02d:00".format(hour),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(RoundedCornerShape(5.dp))
                                        .background(MaterialTheme.colorScheme.primary)
                                )
                            }
                        }
                    }
                }
            }

            /* Divider between timeline and events */
            HorizontalDivider(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            /* Events area */
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
                    .height(timelineHeightInDp + HOUR_HEIGHT_DP / 2)
            ) {
                val minTime = LocalTime.of(TIMELINE_START_HOUR, 0)
                val maxTime = LocalTime.of(TIMELINE_END_HOUR + 1, 0)
                val totalMinutesInTimeline = Duration.between(minTime, maxTime).toMinutes()

                val dpPerMinute = if (totalMinutesInTimeline > 0) {
                    (timelineHeightInDp.value + HOUR_HEIGHT_DP.value / 2) /
                            totalMinutesInTimeline.toFloat()
                } else 0f
                    .dp

                if (dailyEvents.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No events for this day.", style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    dailyEvents.forEach { entry ->
                        val startMinutes = Duration.between(minTime, entry.start).toMinutes()
                        val durationMinutes = Duration.between(entry.start, entry.end).toMinutes()

                        val dpPerMinute = if (totalMinutesInTimeline > 0) {
                            (timelineHeightInDp.value + (HOUR_HEIGHT_DP.value / 2)) / totalMinutesInTimeline.toFloat()
                        } else {
                            0f
                        }

                        val offsetY = (startMinutes * dpPerMinute).dp
                        val height = (durationMinutes * dpPerMinute).dp

                        DailyCourseCard(
                            entry = entry,
                            modifier = Modifier
                                .fillMaxWidth()
                                .offset(y = offsetY + (HOUR_HEIGHT_DP / 2))
                                .height(height)
                        )
                    }
                }
            }
        }
    }
}