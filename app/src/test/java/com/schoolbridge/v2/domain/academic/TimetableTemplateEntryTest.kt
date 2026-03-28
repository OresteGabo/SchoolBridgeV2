package com.schoolbridge.v2.domain.academic

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

class TimetableTemplateEntryTest {

    @Test
    fun `toTimetableEntry maps template onto matching date in same week`() {
        val template = TimetableTemplateEntry(
            id = "math-mon-1",
            dayOfWeek = DayOfWeek.MONDAY,
            startTime = LocalTime.of(8, 0),
            endTime = LocalTime.of(9, 0),
            title = "Mathematics",
            room = "B12",
            teacher = "Mr. Habimana",
            type = TimetableEntryType.LECTURE,
            studentId = "student_1",
            studentName = "Anita"
        )

        val entry = template.toTimetableEntry(LocalDate.of(2026, 3, 25))

        assertEquals(LocalDate.of(2026, 3, 30).atTime(8, 0), entry.start)
        assertEquals(LocalDate.of(2026, 3, 30).atTime(9, 0), entry.end)
        assertEquals("Mathematics", entry.title)
        assertEquals("B12", entry.room)
        assertEquals("Mr. Habimana", entry.teacher)
        assertEquals(TimetableEntryType.LECTURE, entry.type)
        assertEquals("student_1", entry.studentId)
        assertEquals("Anita", entry.studentName)
    }

    @Test
    fun `toTimetableEntry keeps same day when start date already matches target day`() {
        val template = TimetableTemplateEntry(
            id = "assembly-fri",
            dayOfWeek = DayOfWeek.FRIDAY,
            startTime = LocalTime.of(7, 30),
            endTime = LocalTime.of(8, 0),
            title = "Morning Assembly",
            room = "Courtyard",
            type = TimetableEntryType.ASSEMBLY
        )

        val entry = template.toTimetableEntry(LocalDate.of(2026, 4, 3))

        assertEquals(LocalDate.of(2026, 4, 3).atTime(7, 30), entry.start)
        assertEquals(LocalDate.of(2026, 4, 3).atTime(8, 0), entry.end)
    }
}
