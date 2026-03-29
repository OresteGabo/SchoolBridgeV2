package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.MessageConversationDto
import com.schoolbridge.v2.domain.messaging.MessageConversation
import com.schoolbridge.v2.domain.messaging.ConversationMode
import java.time.LocalDateTime

fun MessageConversationDto.toDomain(): MessageConversation = MessageConversation(
    id = id,
    topic = subject,
    participantsLabel = participants.joinToString(", "),
    mode = ConversationMode.entries.firstOrNull { it.name.equals(threadType, ignoreCase = true) }
        ?: ConversationMode.ANNOUNCEMENT,
    messages = messages.map { it.toDomain() }.toMutableList()
)

fun MessageConversation.toDto(): MessageConversationDto = MessageConversationDto(
    id = id,
    subject = topic,
    participants = participantsLabel.split(",").map { it.trim() }.filter { it.isNotEmpty() },
    createdAt = LocalDateTime.now().toString(),
    lastMessageAt = LocalDateTime.now().toString(),
    threadType = mode.name,
    relatedEntityId = null,
    relatedEntityType = null,
    isArchived = false,
    autoDeleteDate = null,
    messages = messages.map { it.toDto() }
)
