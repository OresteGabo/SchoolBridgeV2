package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.domain.academic.TodayCourse
import kotlinx.coroutines.flow.Flow

interface TodayScheduleRepository {
    fun getTodayCourses(): Flow<List<TodayCourse>>
}