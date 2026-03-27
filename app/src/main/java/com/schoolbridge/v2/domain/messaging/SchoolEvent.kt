package com.schoolbridge.v2.domain.messaging

import com.schoolbridge.v2.data.dto.messaging.attachments.InAppAttachmentDto

data class SchoolEvent(
    val id: String,
    val schoolId: String,
    val title: String,
    val description: String,
    val type: String,
    val startDate: String,
    val endDate: String,
    val location: String,
    val recipientType: RecipientType = RecipientType.ALL_USERS,
    val organizerUserId: String,
    val attachments: List<InAppAttachmentDto> = emptyList()
)
