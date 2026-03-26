package com.schoolbridge.v2.data.dto.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    val token: String,
    val tokenType: String,
    val expiresInSeconds: Long,
    val user: LoggedInUserDto
)

@Serializable
data class LoggedInUserDto(
    val id: Long,
    val username: String,
    val firstName: String,
    val familyName: String,
    val email: String,
    val isActive: Boolean,
    val lastLogin: String? = null,
    val roles: List<String> = emptyList()
)
