package com.schoolbridge.v2.data.dto.user.student

import kotlinx.serialization.Serializable

@Serializable
data class LinkedStudentDto(
    val id: String,
    val firstName: String,
    val lastName: String
)