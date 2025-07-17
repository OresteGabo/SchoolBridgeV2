package com.schoolbridge.v2.ui.home.common

import com.schoolbridge.v2.domain.academic.TimetableEntry
import com.schoolbridge.v2.domain.academic.TimetableEntryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

import java.time.LocalDateTime

object LocalTimetableRepo {
    val current = this

    private val _cachedTimetable = MutableStateFlow<List<TimetableEntry>>(emptyList())
    val cachedTimetable: StateFlow<List<TimetableEntry>> = _cachedTimetable

    fun loadMockDataForTesting() {
        val now = LocalDateTime.now()
        _cachedTimetable.value = listOf(
            TimetableEntry(
                id = 1,
                title = "Mathematics",
                start = now.withHour(8).withMinute(0),
                end = now.withHour(9).withMinute(0),
                room = "Room A",
                teacher = "Mr. Ndayisenga",
                type = TimetableEntryType.LECTURE
            ),
            TimetableEntry(
                id = 2,
                title = "English",
                start = now.withHour(9).withMinute(15),
                end = now.withHour(10).withMinute(15),
                room = "Room B",
                teacher = "Mrs. Uwimana",
                type = TimetableEntryType.LECTURE
            ),
            TimetableEntry(
                id = 3,
                title = "Chemistry Lab",
                start = now.withHour(10).withMinute(30),
                end = now.withHour(11).withMinute(30),
                room = "Lab 1",
                teacher = "Mr. Bizimana",
                type = TimetableEntryType.LAB
            )
        )
    }
}

