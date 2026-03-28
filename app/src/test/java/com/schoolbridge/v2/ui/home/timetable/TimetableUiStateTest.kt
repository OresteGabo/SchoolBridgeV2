package com.schoolbridge.v2.ui.home.timetable

import com.schoolbridge.v2.domain.academic.TimetableEntryType
import com.schoolbridge.v2.domain.academic.TimetableTemplateEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TimetableUiStateTest {

    @Test
    fun `dailyAgenda returns timetable and planned items sorted by start time`() {
        val monday = LocalDate.of(2026, 3, 30)
        val uiState = TimetableUiState(
            templates = listOf(
                TimetableTemplateEntry(
                    id = "math",
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(10, 0),
                    endTime = LocalTime.of(11, 0),
                    title = "Mathematics",
                    room = "A2",
                    teacher = "Mr. Karemera",
                    type = TimetableEntryType.LECTURE
                )
            ),
            plannedItems = listOf(
                AgendaItemUi(
                    id = "meeting",
                    start = monday.atTime(8, 30),
                    end = monday.atTime(9, 0),
                    title = "Parent meeting",
                    subtitle = "With class teacher",
                    badge = "Meeting",
                    kind = AgendaItemKind.MEETING,
                    sourceLabel = "Thread plan"
                )
            )
        )

        val agenda = uiState.dailyAgenda(monday)

        assertEquals(listOf("meeting", "class_${"math".hashCode()}"), agenda.map { it.id })
    }

    @Test
    fun `dailyAgenda filters by selected student while keeping shared entries`() {
        val monday = LocalDate.of(2026, 3, 30)
        val uiState = TimetableUiState(
            selectedStudentId = "student_1",
            templates = listOf(
                TimetableTemplateEntry(
                    id = "shared",
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(8, 0),
                    endTime = LocalTime.of(9, 0),
                    title = "Assembly",
                    room = "Main hall",
                    teacher = "",
                    type = TimetableEntryType.ASSEMBLY,
                    studentId = null
                ),
                TimetableTemplateEntry(
                    id = "student_1_only",
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(9, 0),
                    endTime = LocalTime.of(10, 0),
                    title = "Biology",
                    room = "Lab 1",
                    teacher = "Ms. Uwase",
                    type = TimetableEntryType.LAB,
                    studentId = "student_1"
                ),
                TimetableTemplateEntry(
                    id = "student_2_only",
                    dayOfWeek = DayOfWeek.MONDAY,
                    startTime = LocalTime.of(11, 0),
                    endTime = LocalTime.of(12, 0),
                    title = "Physics",
                    room = "Lab 2",
                    teacher = "Mr. Kevin",
                    type = TimetableEntryType.PRACTICAL,
                    studentId = "student_2"
                )
            )
        )

        val agenda = uiState.dailyAgenda(monday)

        assertEquals(listOf("Assembly", "Biology"), agenda.map { it.title })
    }

    @Test
    fun `nowAndNext returns current item and next future item`() {
        val baseDate = LocalDate.of(2026, 3, 30)
        val now = LocalDateTime.of(2026, 3, 30, 9, 30)
        val uiState = TimetableUiState(
            plannedItems = listOf(
                AgendaItemUi(
                    id = "ongoing",
                    start = baseDate.atTime(9, 0),
                    end = baseDate.atTime(10, 0),
                    title = "Ongoing lesson",
                    subtitle = "",
                    badge = "Class",
                    kind = AgendaItemKind.CLASS,
                    sourceLabel = "Timetable"
                ),
                AgendaItemUi(
                    id = "next",
                    start = baseDate.atTime(10, 15),
                    end = baseDate.atTime(11, 0),
                    title = "Next lesson",
                    subtitle = "",
                    badge = "Class",
                    kind = AgendaItemKind.CLASS,
                    sourceLabel = "Timetable"
                )
            )
        )

        val (currentItem, nextItem) = uiState.nowAndNext(from = now)

        assertEquals("ongoing", currentItem?.id)
        assertEquals("next", nextItem?.id)
    }

    @Test
    fun `nextDateWithEvent respects active agenda filters`() {
        val monday = LocalDate.of(2026, 3, 30)
        val uiState = TimetableUiState(
            plannedItems = listOf(
                AgendaItemUi(
                    id = "announcement",
                    start = monday.plusDays(2).atTime(8, 0),
                    end = monday.plusDays(2).atTime(9, 0),
                    title = "Assembly notice",
                    subtitle = "",
                    badge = "Announcement",
                    kind = AgendaItemKind.ANNOUNCEMENT,
                    sourceLabel = "Thread plan"
                )
            )
        )

        val classOnly = uiState.nextDateWithEvent(
            fromDate = monday,
            includedKinds = setOf(AgendaItemKind.CLASS)
        )
        val announcements = uiState.nextDateWithEvent(
            fromDate = monday,
            includedKinds = setOf(AgendaItemKind.ANNOUNCEMENT)
        )

        assertNull(classOnly)
        assertEquals(monday.plusDays(2), announcements)
    }
}
