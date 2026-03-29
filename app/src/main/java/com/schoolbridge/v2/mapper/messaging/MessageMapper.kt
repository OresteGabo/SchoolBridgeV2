package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.MessageInConversationDto
import com.schoolbridge.v2.domain.messaging.Message

fun MessageInConversationDto.toDomain(): Message = Message(
    id = id,
    senderUserId = null,
    sender = senderId,
    content = content,
    timestamp = sentAt,
    isUnread = false,
    isFromCurrentUser = false,
    actions = emptyList(),
    status = null
)

fun Message.toDto(): MessageInConversationDto = MessageInConversationDto(
    id = id,
    senderId = sender,
    content = content,
    sentAt = timestamp,
    readBy = emptyList(),
    inAppAttachments = null
)
