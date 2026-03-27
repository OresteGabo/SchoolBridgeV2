package com.schoolbridge.v2.domain.academic

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

data class TimetableTemplateEntry(
    val id: String,
    val dayOfWeek: DayOfWeek,
    val startTime: LocalTime,
    val endTime: LocalTime,
    val title: String,
    val room: String,
    val teacher: String = "",
    val type: TimetableEntryType,
    val studentId: String? = null,
    val studentName: String? = null,
    val note: String? = null
)

fun TimetableTemplateEntry.toTimetableEntry(startOfWeek: LocalDate): TimetableEntry {
    val date = startOfWeek.with(TemporalAdjusters.nextOrSame(dayOfWeek))
    return TimetableEntry(
        id = id.hashCode(),
        start = date.atTime(startTime),
        end = date.atTime(endTime),
        title = title,
        room = room,
        teacher = teacher,
        type = type,
        studentId = studentId,
        studentName = studentName,
        note = note
    )
}
