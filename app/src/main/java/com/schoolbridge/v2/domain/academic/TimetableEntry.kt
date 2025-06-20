package com.schoolbridge.v2.domain.academic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalTime

/**
 * Domain model for a lesson / lecture displayed in the timetable.
 */
data class TimetableEntry(
    val id: Int,
    val day: DayOfWeek,
    val start: LocalTime,
    val end: LocalTime,
    val title: String,
    val room: String,
    val teacher: String = "",
    val type: TimetableEntryType
)


/* ---------- Constants ---------- */
val HourRange = 7..18    // 12 rows
val DayHeaders = listOf(
    DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
    DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY
)

enum class TimetableEntryType {
    EXAM, TD, TP, COURSE, PRESENTATION
}

@Composable
fun timetableEntryColor(type: TimetableEntryType): Color {
    return when(type) {
        TimetableEntryType.EXAM -> MaterialTheme.colorScheme.errorContainer
        TimetableEntryType.TD -> MaterialTheme.colorScheme.primaryContainer
        TimetableEntryType.TP -> MaterialTheme.colorScheme.secondaryContainer
        TimetableEntryType.COURSE -> MaterialTheme.colorScheme.tertiaryContainer
        TimetableEntryType.PRESENTATION -> MaterialTheme.colorScheme.surfaceVariant
    }
}
