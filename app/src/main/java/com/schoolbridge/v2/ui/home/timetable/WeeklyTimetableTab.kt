package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.academic.DayHeaders
import com.schoolbridge.v2.domain.academic.HourRange
import com.schoolbridge.v2.domain.academic.TimetableEntry
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyTimetableTab(
    initialStartOfWeek: LocalDate,
    onStartOfWeekChange: (LocalDate) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()
    var selected by rememberSaveable { mutableStateOf<TimetableEntry?>(null) }
    var selectedDay by rememberSaveable { mutableStateOf<LocalDate?>(null) }


    var startOfWeek by rememberSaveable { mutableStateOf(initialStartOfWeek) }

    // Generate events dynamically based on current week
    val events = remember(startOfWeek) {
        generateSampleEventsForWeek(startOfWeek)
    }

    val baseSlotH = 64.dp
    val baseDayW = 128.dp
    val baseTimeW = 56.dp

    var scale by remember { mutableFloatStateOf(1f) }
    val minScale = 0.3f
    val maxScale = 3f

    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        BoxWithConstraints(Modifier.fillMaxSize()) {
            val constraints = this.constraints
            val density = LocalDensity.current

            val totalHours = HourRange.count()
            val totalDays = DayHeaders.size

            val minScaleFit = run {
                val totalWidthPx = with(density) { (baseTimeW + baseDayW * totalDays).toPx() }
                val totalHeightPx = with(density) { (baseSlotH * totalHours).toPx() }
                val wFit = constraints.maxWidth.toFloat() / totalWidthPx
                val hFit = constraints.maxHeight.toFloat() / totalHeightPx
                minOf(wFit, hFit).coerceAtMost(maxScale)
            }

            scale = scale.coerceIn(minScaleFit, maxScale)

            val dayW = baseDayW * scale
            val timeW = baseTimeW * scale

            val minSlotHeight = with(density) {
                (constraints.maxHeight.toFloat() / totalHours).toDp()
            }
            val slotH = (baseSlotH * scale).coerceAtLeast(minSlotHeight)

            TimetableScreen(
                events = events,
                slotHeight = slotH,
                dayWidth = dayW,
                timeColWidth = timeW,
                onEventClick = {
                    selected = it
                    scope.launch { sheetState.show() }
                },
                startOfWeek = startOfWeek,
                days = DayHeaders, // ✅ Replace TODO(): This is the standard Mon-Sat list.
                hourRange = HourRange, // ✅ Replace TODO(): Standard hour range (7..20 or whatever you've defined)
                onDayHeaderClick = { day -> // ✅ Replace TODO(): Set selected day and open bottom sheet
                    selectedDay = day
                    selected = null // in case an event was selected, clear it
                    scope.launch { sheetState.show() }
                }
            )

        }

        FloatingTimetableControls(
            onZoomIn = { scale = (scale * 1.12f).coerceIn(minScale, maxScale) },
            onZoomOut = { scale = (scale / 1.12f).coerceIn(minScale, maxScale) },
            modifier = Modifier.align(Alignment.BottomEnd),
            onNavigatePreviousWeek = {
                val newWeek = startOfWeek.minusWeeks(1)
                startOfWeek = newWeek
                onStartOfWeekChange(newWeek)
            },
            onNavigateNextWeek = {
                val newWeek = startOfWeek.plusWeeks(1)
                startOfWeek = newWeek
                onStartOfWeekChange(newWeek)
            }
        )
    }

    if (selected != null) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                selected = null
            }
        ) {
            val e = selected!!
            Column(Modifier.padding(16.dp)) {
                Text(
                    e.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text("Teacher: ${e.teacher.ifBlank { "N/A" }}")
                Text("Room: ${e.room}")
                Text("Date: ${e.start.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy"))}")
                Text("Time: ${e.start.format(DateTimeFormatter.ofPattern("HH:mm"))} – ${e.end.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }
                        selected = null
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }

    if (selectedDay != null) {
        val dayEvents = events.filter { it.start.toLocalDate() == selectedDay }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                scope.launch { sheetState.hide() }
                selectedDay = null
            }
        ) {
            Column(Modifier.padding(16.dp)) {
                Text(
                    text = selectedDay!!.format(DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy")),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                if (dayEvents.isEmpty()) {
                    Text("No events for this day.")
                } else {
                    dayEvents.forEach { e ->
                        Column(Modifier.padding(vertical = 8.dp)) {
                            Text("• ${e.title}", fontWeight = FontWeight.SemiBold)
                            Text("  ${e.start.toLocalTime()} – ${e.end.toLocalTime()}")
                            Text("  ${e.room} | ${e.teacher}")
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        scope.launch { sheetState.hide() }
                        selectedDay = null
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }
        }
    }

}


