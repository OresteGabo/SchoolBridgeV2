
/* ---------- Domain Academic ---------- */
package com.schoolbridge.v2.domain.academic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month

/**
 * Domain model for a lesson / lecture displayed in the timetable.
 */
data class TimetableEntry(
    val id: Int,
    val start: LocalDateTime,
    val end: LocalDateTime,
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
    LECTURE,
    PRACTICAL,
    GROUP_WORK,
    REMEDIAL,
    TEST,
    ASSEMBLY,
    LAB
}


@Composable
fun timetableEntryColor(type: TimetableEntryType): Color {
    return when(type) {
        TimetableEntryType.LECTURE -> MaterialTheme.colorScheme.primaryContainer
        TimetableEntryType.PRACTICAL -> MaterialTheme.colorScheme.secondaryContainer
        TimetableEntryType.GROUP_WORK -> MaterialTheme.colorScheme.tertiaryContainer
        TimetableEntryType.REMEDIAL -> MaterialTheme.colorScheme.errorContainer
        TimetableEntryType.TEST -> MaterialTheme.colorScheme.error
        TimetableEntryType.ASSEMBLY -> MaterialTheme.colorScheme.surfaceVariant
        TimetableEntryType.LAB -> MaterialTheme.colorScheme.surfaceVariant
    }
}

