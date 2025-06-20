package com.schoolbridge.v2.data.dto.user.student

import com.schoolbridge.v2.data.dto.academic.EnrollmentSummaryDto

data class StudentSummaryDto(
    val userId: String,
    val currentEnrollment: EnrollmentSummaryDto
)