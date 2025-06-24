package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.schoolbridge.v2.domain.academic.TimetableEntry
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun DailyTimetableTab(
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val dailyEvents = remember(selectedDate) {
        sampleEvents
            .filter { it.start.toLocalDate() == selectedDate }
            .sortedBy { it.start }
    }

    Column(Modifier.fillMaxSize()) {
        HorizontalDateSelector(
            selectedDate = selectedDate,
            onDateSelected = onDateChange
        )

        Spacer(Modifier.height(16.dp))

        if (dailyEvents.isEmpty()) {
            NoEventsPlaceholder(
                selectedDate = selectedDate,
                onDateChange = onDateChange
            )
        } else {
            TimetableContent(dailyEvents, selectedDate)
        }
    }
}

@Composable
private fun TimetableContent(dailyEvents: List<TimetableEntry>, selectedDate: LocalDate) {
    val listState = rememberLazyListState()
    val hourHeight = 72.dp
    val density = LocalDensity.current

    // Use the earliest event time as the vertical origin
    val startTimeBaseline = dailyEvents.minOf { it.start.toLocalTime() }

    // Auto-scroll to current time if today
    LaunchedEffect(selectedDate) {
        if (selectedDate == LocalDate.now()) {
            val now = LocalTime.now()
            val minutesFromStart = Duration.between(startTimeBaseline, now).toMinutes().toInt()
            val scrollOffset = (minutesFromStart / 60f) * with(density) { hourHeight.toPx() }
            listState.scrollToItem(0, scrollOffset.roundToInt())
        }
    }

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize()
            .padding(horizontal = 12.dp),
    ) {
        items(dailyEvents) { entry ->

            DailyCourseCard(
                entry = entry,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}


@Composable
fun NoEventsPlaceholder(
    modifier: Modifier = Modifier,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val nextDateWithEvent = remember(selectedDate) {
        (1..30).map { selectedDate.plusDays(it.toLong()) }
            .firstOrNull { date ->
                sampleEvents.any { it.start.toLocalDate() == date }
            }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.widthIn(max = 320.dp)
        ) {
            Icon(
                imageVector = Icons.Default.EventBusy,
                contentDescription = "No events icon",
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                modifier = Modifier
                    .size(96.dp)
                    .padding(bottom = 8.dp)
            )

            Text(
                "No events for this day",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

            Text(
                "Looks like you're free today.",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(Modifier.height(16.dp))

            if (nextDateWithEvent != null) {
                OutlinedButton(onClick = { onDateChange(nextDateWithEvent) }) {
                    Icon(Icons.Default.Schedule, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Go to next event")
                }
            } else {
                Text(
                    "No upcoming events found.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

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

    // Keep in sync with selectedDate
    LaunchedEffect(selectedDate) {
        if (selectedDate != selectedDay) {
            currentMonth = YearMonth.from(selectedDate)
            selectedDay = selectedDate
        }
    }

    val daysInMonth = remember(currentMonth) {
        (1..currentMonth.lengthOfMonth()).map { day -> currentMonth.atDay(day) }
    }

    val itemWidth by animateDpAsState(targetValue = if (compact) 40.dp else 60.dp)
    val spacing by animateDpAsState(targetValue = if (compact) 4.dp else 8.dp)
    val padding by animateDpAsState(targetValue = if (compact) 4.dp else 8.dp)

    // Scroll to selected day
    LaunchedEffect(selectedDay, daysInMonth) {
        val index = daysInMonth.indexOfFirst { it == selectedDay }
        if (index >= 0) {
            val screenWidthPx = with(density) { screenWidthDp.toPx() }
            val itemWidthPx = with(density) { itemWidth.toPx() }
            val offset = (screenWidthPx / 2 - itemWidthPx / 2).toInt()
            listState.animateScrollToItem(index, scrollOffset = -offset)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
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
                    modifier = Modifier.weight(1f)
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
