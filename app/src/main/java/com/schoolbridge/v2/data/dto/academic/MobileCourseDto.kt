package com.schoolbridge.v2.data.dto.academic

import kotlinx.serialization.Serializable

@Serializable
data class MobileCourseFeedDto(
    val audience: String,
    val scopeLabel: String? = null,
    val students: List<MobileCourseStudentDto> = emptyList(),
    val selectedStudentId: String? = null,
    val courses: List<MobileCourseDto> = emptyList()
)

@Serializable
data class MobileCourseStudentDto(
    val id: String,
    val name: String,
    val firstName: String? = null,
    val lastName: String? = null
)

@Serializable
data class MobileCourseDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val teacherName: String? = null,
    val teacherUserId: String? = null,
    val room: String? = null,
    val dayOfWeek: String? = null,
    val startTime: String? = null,
    val endTime: String? = null,
    val entryType: String? = null,
    val studentId: String? = null,
    val studentName: String? = null
)
