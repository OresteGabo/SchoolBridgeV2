package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.MessageInThreadDto
import com.schoolbridge.v2.domain.messaging.Message

fun MessageInThreadDto.toDomain(): Message = Message(
    id = id,
    sender = senderId,
    content = content,
    timestamp = sentAt,
    isUnread = false,
    actions = emptyList(),
    status = null
)

fun Message.toDto(): MessageInThreadDto = MessageInThreadDto(
    id = id,
    senderId = sender,
    content = content,
    sentAt = timestamp,
    readBy = emptyList(),
    inAppAttachments = null
)
