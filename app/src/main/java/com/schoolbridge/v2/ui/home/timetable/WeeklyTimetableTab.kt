package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyTimetableTab(
    uiState: TimetableUiState,
    startOfWeek: LocalDate,
    selectedDate: LocalDate,
    includedKinds: Set<AgendaItemKind>,
    density: AgendaDensity,
    emptyDayMessage: String,
    onSelectedDateChange: (LocalDate) -> Unit,
    onStartOfWeekChange: (LocalDate) -> Unit
) {
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val today = LocalDate.now()
    val effectiveSelectedDate = remember(weekDays, selectedDate, today) {
        when {
            weekDays.contains(selectedDate) -> selectedDate
            weekDays.contains(today) -> today
            else -> weekDays.first()
        }
    }
    val visibleDate by remember(weekDays, listState) {
        derivedStateOf {
            weekDays.getOrNull(listState.firstVisibleItemIndex) ?: effectiveSelectedDate
        }
    }

    LaunchedEffect(startOfWeek, effectiveSelectedDate) {
        val selectedIndex = weekDays.indexOf(effectiveSelectedDate)
        if (selectedIndex >= 0) {
            listState.scrollToItem(selectedIndex)
        } else {
            listState.scrollToItem(0)
        }
    }

    LaunchedEffect(visibleDate) {
        visibleDate?.let(onSelectedDateChange)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        WeeklyDayStrip(
            weekDays = weekDays,
            uiState = uiState,
            includedKinds = includedKinds,
            selectedDate = visibleDate ?: effectiveSelectedDate,
            onDaySelected = { date ->
                val index = weekDays.indexOf(date).coerceAtLeast(0)
                onSelectedDateChange(date)
                scope.launch { listState.animateScrollToItem(index) }
            }
        )

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(weekDays, key = { it.toString() }) { date ->
                val dayAgenda = uiState.dailyAgenda(date, includedKinds)
                WeeklyDayCard(
                    date = date,
                    agendaItems = dayAgenda,
                    density = density,
                    emptyDayMessage = emptyDayMessage
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedButton(onClick = { onStartOfWeekChange(startOfWeek.minusWeeks(1)) }) {
                        Text("Previous week")
                    }
                    OutlinedButton(onClick = { onStartOfWeekChange(startOfWeek.plusWeeks(1)) }) {
                        Text("Next week")
                    }
                }
            }
        }
    }
}

@Composable
private fun WeeklyDayStrip(
    weekDays: List<LocalDate>,
    uiState: TimetableUiState,
    includedKinds: Set<AgendaItemKind>,
    selectedDate: LocalDate?,
    onDaySelected: (LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        weekDays.forEach { date ->
            val items = uiState.dailyAgenda(date, includedKinds)
            val isSelected = date == selectedDate
            val containerColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.primaryContainer
                } else {
                    MaterialTheme.colorScheme.surfaceContainerLow
                },
                label = "week_chip_container"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                label = "week_chip_content"
            )
            val scale by animateDpAsState(
                targetValue = if (isSelected) 1.dp else 0.dp,
                label = "week_chip_scale"
            )

            AssistChip(
                onClick = { onDaySelected(date) },
                label = {
                    Text(
                        "${date.shortWeekdayLabel()} ${date.dayOfMonth} • ${items.size}",
                        color = contentColor
                    )
                },
                colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
                    containerColor = containerColor,
                    labelColor = contentColor
                ),
                modifier = Modifier.scale(1f + scale.value * 0.03f)
            )
        }
    }
}

@Composable
private fun WeeklyDayCard(
    date: LocalDate,
    agendaItems: List<AgendaItemUi>,
    density: AgendaDensity,
    emptyDayMessage: String
) {
    var expanded by rememberSaveable(date.toString()) { mutableStateOf(false) }
    val visibleItems = if (expanded) agendaItems else agendaItems.take(3)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE, MMM d")),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (agendaItems.isEmpty()) {
                Text(
                    text = emptyDayMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                visibleItems.forEach { item ->
                    AgendaCard(item = item, density = density)
                }
                if (agendaItems.size > 3) {
                    OutlinedButton(onClick = { expanded = !expanded }) {
                        Text(
                            if (expanded) "Show less"
                            else "+${agendaItems.size - 3} more planned items"
                        )
                    }
                }
            }
        }
    }
}
