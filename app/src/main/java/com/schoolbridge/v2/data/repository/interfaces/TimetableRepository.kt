package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.academic.MobileTimetableResponseDto

interface TimetableRepository {
    suspend fun getTimetable(): MobileTimetableResponseDto
}
