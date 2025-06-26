package com.schoolbridge.v2.data.repository.impl

import com.schoolbridge.v2.data.repository.interfaces.TodayScheduleRepository
import com.schoolbridge.v2.domain.academic.TodayCourse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TodayScheduleRepositoryImpl : TodayScheduleRepository {
    override fun getTodayCourses(): Flow<List<TodayCourse>> = flow {
        emit(
            listOf(
                TodayCourse("Mathematics", "08:00", "09:40", "Mr. Kamali", "Room A1"),
                TodayCourse("Chemistry", "10:00", "11:40", "Ms. Uwase", "Lab 3"),
                TodayCourse("History", "13:00", "14:40", "Mr. Habimana", "Room B2")
            )
        )
    }
}
