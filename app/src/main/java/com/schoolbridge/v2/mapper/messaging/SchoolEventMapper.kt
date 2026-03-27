package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.SchoolEventDto
import com.schoolbridge.v2.domain.messaging.RecipientType
import com.schoolbridge.v2.domain.messaging.SchoolEvent

fun SchoolEventDto.toDomain(): SchoolEvent = SchoolEvent(
    id = id,
    schoolId = schoolId,
    title = title,
    description = description,
    type = type,
    startDate = startDate,
    endDate = endDate,
    location = location,
    recipientType = RecipientType.fromRaw(targetAudience),
    organizerUserId = organizerUserId,
    attachments = inAppAttachments.orEmpty()
)

fun SchoolEvent.toDto(): SchoolEventDto = SchoolEventDto(
    id = id,
    schoolId = schoolId,
    title = title,
    description = description,
    type = type,
    startDate = startDate,
    endDate = endDate,
    location = location,
    targetAudience = recipientType.name,
    organizerUserId = organizerUserId,
    inAppAttachments = attachments.ifEmpty { null }
)
