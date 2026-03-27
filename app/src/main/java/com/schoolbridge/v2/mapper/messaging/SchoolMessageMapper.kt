package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.SchoolMessageDto
import com.schoolbridge.v2.domain.messaging.RecipientType
import com.schoolbridge.v2.domain.messaging.SchoolMessage

fun SchoolMessageDto.toDomain(): SchoolMessage = SchoolMessage(
    id = id,
    schoolId = schoolId,
    authorUserId = authorUserId,
    subject = subject,
    content = content,
    category = category,
    recipientType = RecipientType.fromRaw(targetAudience),
    publishedAt = publishedDate,
    expiresAt = expiryDate,
    attachments = inAppAttachments.orEmpty()
)

fun SchoolMessage.toDto(): SchoolMessageDto = SchoolMessageDto(
    id = id,
    schoolId = schoolId,
    authorUserId = authorUserId,
    subject = subject,
    content = content,
    category = category,
    targetAudience = recipientType.name,
    publishedDate = publishedAt,
    expiryDate = expiresAt,
    inAppAttachments = attachments.ifEmpty { null }
)
