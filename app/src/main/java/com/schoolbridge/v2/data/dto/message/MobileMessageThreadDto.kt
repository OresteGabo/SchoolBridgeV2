package com.schoolbridge.v2.data.dto.message

import kotlinx.serialization.Serializable

@Serializable
data class MobileMessageThreadDto(
    val id: String,
    val topic: String,
    val participantsLabel: String,
    val conversationType: String = "",
    val mode: String,
    val lastUpdatedAt: String,
    val unreadCount: Int,
    val calls: List<MobileThreadCallSummaryDto> = emptyList(),
    val messages: List<MobileMessageDto>
)

@Serializable
data class MobileMessageDto(
    val id: String,
    val title: String? = null,
    val senderUserId: String? = null,
    val sender: String,
    val content: String,
    val timestamp: String,
    val status: String? = null,
    val isUnread: Boolean = false,
    val replyToId: String? = null,
    val replyToContent: String? = null,
    val replyToSender: String? = null,
    val actions: List<MobileMessageActionDto> = emptyList()
)

@Serializable
data class MobileMessageActionDto(
    val label: String,
    val actionId: String
)

@Serializable
data class MobileThreadCallSummaryDto(
    val id: String,
    val title: String,
    val note: String? = null,
    val type: String,
    val purpose: String,
    val status: String,
    val hostLabel: String,
    val scheduledFor: String? = null,
    val startedAt: String? = null,
    val endedAt: String? = null,
    val scheduledLabel: String? = null,
    val durationLabel: String? = null,
    val relatedMessageId: String? = null,
    val participantSummary: String? = null
)
