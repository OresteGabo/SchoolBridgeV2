
/* ---------- Domain Academic ---------- */
package com.schoolbridge.v2.domain.academic

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.Month

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

// Sample events for preview and testing
val sampleEvents = listOf(
    TimetableEntry(1, DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(9, 30), "Maths", "Room 101", "Mr. Smith", TimetableEntryType.COURSE),
    TimetableEntry(2, DayOfWeek.MONDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), "Physics Lab", "Lab 2", "Ms. Jones", TimetableEntryType.TP),
    TimetableEntry(3, DayOfWeek.MONDAY, LocalTime.of(11, 30), LocalTime.of(12, 30), "History TD", "Room 205", "Dr. Brown", TimetableEntryType.TD),
    TimetableEntry(4, DayOfWeek.MONDAY, LocalTime.of(14, 0), LocalTime.of(15, 30), "Chemistry", "Room 301", "Prof. Davis", TimetableEntryType.COURSE),
    TimetableEntry(5, DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "English", "Room 102", "Ms. White", TimetableEntryType.COURSE),
    TimetableEntry(6, DayOfWeek.TUESDAY, LocalTime.of(10, 30), LocalTime.of(12, 0), "Biology", "Lab 3", "Mr. Green", TimetableEntryType.COURSE),
    TimetableEntry(7, DayOfWeek.WEDNESDAY, LocalTime.of(8, 30), LocalTime.of(10, 0), "Computer Science", "Room 401", "Dr. Black", TimetableEntryType.COURSE),
    TimetableEntry(8, DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(11, 0), "Oral Exam", "Room 103", "Prof. Turner", TimetableEntryType.EXAM),
    TimetableEntry(9, DayOfWeek.THURSDAY, LocalTime.of(13, 0), LocalTime.of(14, 30), "Art Presentation", "Auditorium", "Ms. Grey", TimetableEntryType.PRESENTATION),
    TimetableEntry(10, DayOfWeek.FRIDAY, LocalTime.of(9, 0), LocalTime.of(10, 0), "PE", "Gym", "", TimetableEntryType.COURSE)
)