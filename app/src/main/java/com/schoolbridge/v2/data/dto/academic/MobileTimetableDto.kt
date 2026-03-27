package com.schoolbridge.v2.data.dto.academic

import kotlinx.serialization.Serializable

@Serializable
data class MobileTimetableResponseDto(
    val audience: String,
    val scopeLabel: String? = null,
    val students: List<MobileTimetableStudentDto> = emptyList(),
    val selectedStudentId: String? = null,
    val entries: List<MobileTimetableEntryDto> = emptyList()
)

@Serializable
data class MobileTimetableStudentDto(
    val id: String,
    val name: String,
    val firstName: String? = null,
    val lastName: String? = null
)

@Serializable
data class MobileTimetableEntryDto(
    val id: String,
    val title: String,
    val room: String? = null,
    val teacher: String? = null,
    val dayOfWeek: String,
    val startTime: String,
    val endTime: String,
    val type: String,
    val studentId: String? = null,
    val studentName: String? = null,
    val note: String? = null
)
