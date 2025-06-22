package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.academic.TimetableEntry
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.unit.IntSize


@Composable
fun HorizontalDateSelector(
    selectedDate: LocalDate = LocalDate.now(),
    onDateSelected: (LocalDate) -> Unit,
    compact: Boolean = false
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val screenWidthDp: Dp = configuration.screenWidthDp.dp
    val density = LocalDensity.current

    var currentMonth by remember { mutableStateOf(YearMonth.from(selectedDate)) }
    var selectedDay by remember { mutableStateOf(selectedDate) }

    val daysInMonth = remember(currentMonth) {
        (1..currentMonth.lengthOfMonth()).map { day -> currentMonth.atDay(day) }
    }

    val itemWidth by animateDpAsState(targetValue = if (compact) 40.dp else 60.dp)
    val spacing by animateDpAsState(targetValue = if (compact) 4.dp else 8.dp)
    val padding by animateDpAsState(targetValue = if (compact) 4.dp else 8.dp)

    LaunchedEffect(currentMonth, selectedDay) {
        val index = daysInMonth.indexOfFirst { it == selectedDay }
        if (index >= 0) {
            val screenWidthPx = with(density) { screenWidthDp.toPx() }
            val itemWidthPx = with(density) { itemWidth.toPx() }
            val offset = (screenWidthPx / 2 - itemWidthPx / 2).toInt()
            listState.animateScrollToItem(index, scrollOffset = -offset)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {

        /** Month navigation header **/
        AnimatedVisibility(visible = !compact) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    currentMonth = currentMonth.minusMonths(1)
                    selectedDay = currentMonth.atDay(1)
                    onDateSelected(selectedDay)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
                }

                Text(
                    text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick = {
                    currentMonth = currentMonth.plusMonths(1)
                    selectedDay = currentMonth.atDay(1)
                    onDateSelected(selectedDay)
                }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
                }
            }
        }

        /** Day items **/
        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(spacing),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(daysInMonth) { index, date ->
                val isSelected = date == selectedDay

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(itemWidth)
                        .clip(MaterialTheme.shapes.small)
                        .clickable {
                            selectedDay = date
                            onDateSelected(date)
                            coroutineScope.launch {
                                val screenWidthPx = with(density) { screenWidthDp.toPx() }
                                val itemWidthPx = with(density) { itemWidth.toPx() }
                                val offset = (screenWidthPx / 2 - itemWidthPx / 2).toInt()
                                listState.animateScrollToItem(index, scrollOffset = -offset)
                            }
                        }
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                            else MaterialTheme.colorScheme.surface
                        )
                        .border(
                            width = 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant,
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(padding)
                ) {
                    Text(
                        text = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = date.dayOfMonth.toString(),
                        style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

private fun centerLazyRowItem(
    listState: LazyListState,
    index: Int,
    scope: CoroutineScope,
    density: Density,
    screenWidth: IntSize,
    itemWidth: Dp
) {
    scope.launch {
        val itemPx = with(density) { itemWidth.toPx() }
        val centerOffset = (screenWidth.width / 2f - itemPx / 2f).toInt()
        listState.animateScrollToItem(index, scrollOffset = -centerOffset)
    }
}


/* ────────────────────────────────────────────────────────────────────────── */
/* 2.  Daily timetable screen                                                */
/* ────────────────────────────────────────────────────────────────────────── */

val MINUTE_HEIGHT_DP = 1.2.dp
const val TIMELINE_START_HOUR = 7
const val TIMELINE_END_HOUR   = 18          // inclusive
val HOUR_HEIGHT_DP = MINUTE_HEIGHT_DP * 60f // 1 hour → 72 dp with default

@Composable
fun DailyTimetableTab(
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    /* Show 30 days back and 30 days forward relative to *today*      */
    val visibleDates = remember(selectedDate) {
        (-30..30).map { LocalDate.now().plusDays(it.toLong()) }
    }

    val verticalScrollState = rememberScrollState()

    Column(Modifier.fillMaxSize()) {

        /*  Calendar strip  */
        HorizontalDateSelector(
            selectedDate  = selectedDate,
            //visibleDates  = visibleDates,
            onDateSelected = onDateChange
        )

        Spacer(Modifier.height(16.dp))

        /* Filter events for this day (replace `sampleEvents` with your own) */
        val dailyEvents = remember(selectedDate) {
            sampleEvents
                .filter { it.day == selectedDate.dayOfWeek }
                .sortedBy { it.start }
        }

        /* Time-line + events */
        Row(
            Modifier
                .fillMaxSize()
                .verticalScroll(verticalScrollState)
        ) {
            val totalHours       = (TIMELINE_END_HOUR - TIMELINE_START_HOUR) + 1
            val timelineHeightDp = HOUR_HEIGHT_DP * totalHours

            /* --- Timeline labels + vertical line --- */
            TimeAxis(timelineHeightDp)

            /* Divider */
            HorizontalDivider(
                Modifier
                    .fillMaxHeight()
                    .width(1.dp)
                    .padding(vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )

            /* Events area */
            EventsColumn(
                dailyEvents        = dailyEvents,
                timelineHeightDp   = timelineHeightDp
            )
        }
    }
}

/* ────────────────────────────────────────────────────────────────────────── */
/* Helper: draw timeline hours / dots                                        */
/* ────────────────────────────────────────────────────────────────────────── */

@Composable
private fun TimeAxis(timelineHeightDp: Dp) {
    Box(
        modifier = Modifier
            .width(IntrinsicSize.Min)
            .padding(start = 8.dp, end = 4.dp)
            .height(timelineHeightDp + HOUR_HEIGHT_DP / 2)
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
                color       = lineColor,
                start       = Offset(centerX, 0f),
                end         = Offset(centerX, size.height),
                strokeWidth = 4f,
                cap         = StrokeCap.Round
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
                            "%02d:00".format(hour),
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
}

/* ────────────────────────────────────────────────────────────────────────── */
/* Helper: column that positions each event card                             */
/* ────────────────────────────────────────────────────────────────────────── */

@Composable
private fun EventsColumn(
    dailyEvents: List<TimetableEntry>,
    timelineHeightDp: Dp,
    modifier: Modifier = Modifier
) {
    val minTime = LocalTime.of(TIMELINE_START_HOUR, 0)
    val maxTime = LocalTime.of(TIMELINE_END_HOUR + 1, 0)
    val totalMinutes = Duration.between(minTime, maxTime).toMinutes().toFloat()

    val totalHeight = timelineHeightDp + (HOUR_HEIGHT_DP / 2)
    val dpPerMinute = if (totalMinutes > 0) {
        totalHeight / totalMinutes
    } else {
        0.dp
    }

    Box(
        modifier = modifier
            .padding(horizontal = 12.dp)
            .height(totalHeight)
    ) {
        if (dailyEvents.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No events for this day.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            dailyEvents.forEach { entry ->
                val startMinutes = Duration.between(minTime, entry.start).toMinutes().toInt()
                val durationMinutes = Duration.between(entry.start, entry.end).toMinutes().toInt()

                val offsetY = dpPerMinute * startMinutes
                val height = dpPerMinute * durationMinutes

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


/* ────────────────────────────────────────────────────────────────────────── */
/*  Dummy data + DailyCourseCard declaration   (keep existing in your code)  */
/* ────────────────────────────────────────────────────────────────────────── */

// Replace `TimetableEntry`, `sampleEvents`, and `DailyCourseCard`
// with the ones you already have in your project.
