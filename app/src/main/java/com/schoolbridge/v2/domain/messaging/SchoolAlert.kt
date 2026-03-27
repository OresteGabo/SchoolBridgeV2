package com.schoolbridge.v2.domain.messaging

import com.schoolbridge.v2.data.dto.messaging.attachments.InAppAttachmentDto

data class SchoolAlert(
    val id: String,
    val schoolId: String,
    val createdByUserId: String,
    val recipientType: RecipientType = RecipientType.ALL_USERS,
    val type: AlertType = AlertType.ANNOUNCEMENT,
    val title: String,
    val content: String,
    val publishedAt: String,
    val expiresAt: String? = null,
    val attachments: List<InAppAttachmentDto> = emptyList()
)
