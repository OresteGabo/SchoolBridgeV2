package com.schoolbridge.v2.data.repository.implementations

import com.schoolbridge.v2.data.dto.academic.CreatePersonalTimetablePlanRequestDto
import com.schoolbridge.v2.data.dto.academic.MobilePersonalTimetablePlanDto
import com.schoolbridge.v2.data.dto.academic.MobileTimetableResponseDto
import com.schoolbridge.v2.data.remote.TimetableApiService
import com.schoolbridge.v2.data.repository.interfaces.TimetableRepository

class TimetableRepositoryImpl(
    private val timetableApiService: TimetableApiService
) : TimetableRepository {
    override suspend fun getTimetable(): MobileTimetableResponseDto = timetableApiService.getTimetable()

    override suspend fun createPersonalPlan(request: CreatePersonalTimetablePlanRequestDto): MobilePersonalTimetablePlanDto =
        timetableApiService.createPersonalPlan(request)

    override suspend fun updatePersonalPlan(planId: Long, request: CreatePersonalTimetablePlanRequestDto): MobilePersonalTimetablePlanDto =
        timetableApiService.updatePersonalPlan(planId, request)

    override suspend fun deletePersonalPlan(planId: Long) {
        timetableApiService.deletePersonalPlan(planId)
    }
}
