package com.schoolbridge.v2.data.dto.academic

import kotlinx.serialization.Serializable

@Serializable
data class CreatePersonalTimetablePlanRequestDto(
    val title: String,
    val description: String? = null,
    val date: String,
    val startTime: String,
    val endTime: String,
    val type: String,
    val visibility: String = "PRIVATE",
    val participantUserIds: List<Long> = emptyList(),
    val note: String? = null
)
