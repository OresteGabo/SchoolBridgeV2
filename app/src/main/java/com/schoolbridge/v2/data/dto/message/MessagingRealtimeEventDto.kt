package com.schoolbridge.v2.data.dto.message

import kotlinx.serialization.Serializable

@Serializable
data class MessagingRealtimeEventDto(
    val eventType: String,
    val conversationId: Long? = null,
    val messageId: Long? = null,
    val actorUserId: Long? = null,
    val title: String,
    val body: String,
    val occurredAt: String
)

@Serializable
data class CreateMessageRequestDto(
    val conversationId: Long,
    val senderId: Long? = null,
    val content: String,
    val type: String = "TEXT"
)

@Serializable
data class MarkMessageReadRequestDto(
    val userId: Long
)
