package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.MessageThreadDto
import com.schoolbridge.v2.domain.messaging.MessageThread
import com.schoolbridge.v2.domain.messaging.ThreadMode
import java.time.LocalDateTime

fun MessageThreadDto.toDomain(): MessageThread = MessageThread(
    id = id,
    topic = subject,
    participantsLabel = participants.joinToString(", "),
    mode = ThreadMode.entries.firstOrNull { it.name.equals(threadType, ignoreCase = true) }
        ?: ThreadMode.ANNOUNCEMENT,
    messages = messages.map { it.toDomain() }.toMutableList()
)

fun MessageThread.toDto(): MessageThreadDto = MessageThreadDto(
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
