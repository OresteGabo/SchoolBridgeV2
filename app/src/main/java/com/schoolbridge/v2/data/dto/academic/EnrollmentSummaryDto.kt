package com.schoolbridge.v2.data.dto.academic

data class EnrollmentSummaryDto(
    val academicYear: String,
    val schoolLevel: String,
    val courseIds: List<String>,
    val teacherUserIds: List<String>
)