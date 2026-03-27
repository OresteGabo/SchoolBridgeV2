package com.schoolbridge.v2.domain.messaging

import com.schoolbridge.v2.data.dto.messaging.attachments.InAppAttachmentDto

data class SchoolMessage(
    val id: String,
    val schoolId: String,
    val authorUserId: String,
    val subject: String,
    val content: String,
    val category: String? = null,
    val recipientType: RecipientType = RecipientType.ALL_USERS,
    val publishedAt: String,
    val expiresAt: String? = null,
    val attachments: List<InAppAttachmentDto> = emptyList()
)
