package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DailyTimetableTab(
    agendaItems: List<AgendaItemUi>,
    nextDateWithEvent: LocalDate?,
    selectedDate: LocalDate,
    onDateChange: (LocalDate) -> Unit,
    density: AgendaDensity,
    emptyTitle: String,
    emptyMessage: String
) {
    Column(Modifier.fillMaxSize()) {
        AgendaDateRibbon(
            selectedDate = selectedDate,
            onDateSelected = onDateChange
        )

        Spacer(Modifier.height(14.dp))

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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(agendaItems, key = { it.id }) { item ->
                    AgendaCard(item = item, density = density)
                }
            }
        }
    }
}

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
