package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.zIndex
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.runtime.Composable
import androidx.compose.ui.draw.clip
import com.schoolbridge.v2.domain.academic.*
import java.time.LocalDate
import kotlin.math.max
@Composable
fun TimetableScreen(
    events: List<TimetableEntry>,
    slotHeight: Dp,
    dayWidth: Dp,
    timeColWidth: Dp,
    onEventClick: (TimetableEntry) -> Unit,
    days: List<DayOfWeek> = DayHeaders,
    hourRange: IntRange = HourRange,
    startOfWeek: LocalDate,
    onDayHeaderClick: (LocalDate) -> Unit
) {
    val headerHeight = 40.dp
    val hScroll = rememberScrollState()
    val vScroll = rememberScrollState()

    Box(Modifier.fillMaxSize()) {
        // 1. Day headers
        DayHeaderRow(
            days = days,
            dayWidth = dayWidth,
            timeColWidth = timeColWidth,
            headerHeight = headerHeight,
            hScroll = hScroll,
            startOfWeek = startOfWeek, // PASS THE NEW PARAMETER
            onDayHeaderClick = onDayHeaderClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = timeColWidth)

        )

        // 2. Hour column
        HourColumn(
            hourRange,
            slotHeight,
            timeColWidth,
            headerHeight,
            vScroll,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = headerHeight)
        )

        // 3. Grid and events with scrolling
        TimetableGridAndEvents(
            days = days,
            hourRange = hourRange,
            events = events,
            slotHeight = slotHeight,
            dayWidth = dayWidth,
            timeColWidth = timeColWidth,
            headerHeight = headerHeight,
            hScroll = hScroll,
            vScroll = vScroll,
            onEventClick = onEventClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = timeColWidth, top = headerHeight)
        )
    }
}

@Composable
private fun DayHeaderRow(
    days: List<DayOfWeek>,
    dayWidth: Dp,
    timeColWidth: Dp,
    headerHeight: Dp,
    hScroll: ScrollState,
    startOfWeek: LocalDate,
    onDayHeaderClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .horizontalScroll(hScroll)
            .height(headerHeight)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .zIndex(2f)
    ) {
        days.forEachIndexed { index, dayOfWeek ->
            val date = startOfWeek.plusDays(index.toLong())
            Box(
                Modifier
                    .width(dayWidth)
                    .fillMaxHeight()
                    .clickable { onDayHeaderClick(date) } // ✅ handle click
                    .border(0.5.dp, MaterialTheme.colorScheme.outline),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        date.dayOfMonth.toString(),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}


@Composable
private fun DayHeaderRow(
    days: List<DayOfWeek>,
    dayWidth: Dp,
    timeColWidth: Dp,
    headerHeight: Dp,
    hScroll: ScrollState,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .horizontalScroll(hScroll)
            .height(headerHeight)
            .background(MaterialTheme.colorScheme.primaryContainer)
            .zIndex(2f)
    ) {
        days.forEach { day ->
            Box(
                Modifier
                    .width(dayWidth)
                    .fillMaxHeight()
                    .border(0.5.dp, MaterialTheme.colorScheme.outline),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    day.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
private fun HourColumn(
    hourRange: IntRange,
    slotHeight: Dp,
    timeColWidth: Dp,
    headerHeight: Dp,
    vScroll: ScrollState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .verticalScroll(vScroll)
            .width(timeColWidth)
            .zIndex(2f)
            .background(MaterialTheme.colorScheme.surfaceContainerLow)
            .padding(top = 0.dp) // padding handled outside via align + padding
    ) {
        hourRange.forEach { hour ->
            Box(
                Modifier
                    .height(slotHeight)
                    .fillMaxWidth()
                    .border(0.5.dp, MaterialTheme.colorScheme.outline),
                contentAlignment = Alignment.TopStart
            ) {
                Text(
                    text = "%02dh".format(hour),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(start = 4.dp, top = 2.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun TimetableGridAndEvents(
    days: List<DayOfWeek>,
    hourRange: IntRange,
    events: List<TimetableEntry>,
    slotHeight: Dp,
    dayWidth: Dp,
    timeColWidth: Dp,
    headerHeight: Dp,
    hScroll: ScrollState,
    vScroll: ScrollState,
    onEventClick: (TimetableEntry) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    Box(
        modifier
            .horizontalScroll(hScroll)
            .verticalScroll(vScroll)
            .clipToBounds()
    ) {
        // Background grid
        Row {
            days.forEach { _ ->
                Column {
                    hourRange.forEach { _ ->
                        Box(
                            Modifier
                                .size(dayWidth, slotHeight)
                                .border(0.5.dp, MaterialTheme.colorScheme.outline)
                                .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                        )
                    }
                }
            }
        }

        // Event cards
        val firstHour = hourRange.first
        events.forEach { entry ->
            val dayIdx = days.indexOf(entry.start.dayOfWeek)
            if (dayIdx == -1) return@forEach

            val startMinutes = entry.start.hour * 60 + entry.start.minute
            val endMinutes = entry.end.hour * 60 + entry.end.minute
            val topMinutes = startMinutes - firstHour * 60
            val durationMinutes = max(15, endMinutes - startMinutes)

            val topPx = with(density) { (topMinutes / 60f * slotHeight.toPx()) }
            val heightPx = with(density) { (durationMinutes / 60f * slotHeight.toPx()) }
            val leftPx = with(density) { (dayIdx * dayWidth.toPx()) }

            Box(
                Modifier
                    .absoluteOffset(
                        x = with(density) { leftPx.toDp() },
                        y = with(density) { topPx.toDp() }
                    )
                    .width(dayWidth)
                    .height(with(density) { heightPx.toDp() })
                    .padding(2.dp)
                    .clickable { onEventClick(entry) }
                    .zIndex(1f)
            ) {
                TimetableEventCard(
                    entry = entry,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
















