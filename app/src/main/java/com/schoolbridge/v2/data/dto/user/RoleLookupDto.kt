package com.schoolbridge.v2.data.dto.user

import kotlinx.serialization.Serializable

@Serializable
data class SchoolLookupDto(
    val id: Long,
    val name: String,
    val abbrevName: String? = null,
    val sectorName: String? = null,
    val isPublic: Boolean,
    val hasBoarding: Boolean
)

@Serializable
data class StudentLookupDto(
    val studentUserId: Long,
    val fullName: String,
    val schoolId: Long,
    val schoolName: String,
    val academicLevel: String,
    val combinationName: String? = null
)
