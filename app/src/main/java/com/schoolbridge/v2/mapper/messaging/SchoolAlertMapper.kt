package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.SchoolAlertDto
import com.schoolbridge.v2.domain.messaging.AlertType
import com.schoolbridge.v2.domain.messaging.RecipientType
import com.schoolbridge.v2.domain.messaging.SchoolAlert

fun SchoolAlertDto.toDomain(): SchoolAlert = SchoolAlert(
    id = id,
    schoolId = schoolId,
    createdByUserId = createdByUserId,
    recipientType = RecipientType.fromRaw(targetAudience),
    type = AlertType.entries.firstOrNull { it.name.equals(type, ignoreCase = true) } ?: AlertType.ANNOUNCEMENT,
    title = title,
    content = content,
    publishedAt = publishedDate,
    expiresAt = expiryDate,
    attachments = inAppAttachments.orEmpty()
)

fun SchoolAlert.toDto(): SchoolAlertDto = SchoolAlertDto(
    id = id,
    schoolId = schoolId,
    createdByUserId = createdByUserId,
    targetAudience = recipientType.name,
    type = type.name,
    title = title,
    content = content,
    publishedDate = publishedAt,
    expiryDate = expiresAt,
    inAppAttachments = attachments.ifEmpty { null }
)
