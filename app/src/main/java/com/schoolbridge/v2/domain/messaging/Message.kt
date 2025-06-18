package com.schoolbridge.v2.domain.messaging

import java.time.LocalDateTime
import java.util.UUID

/**
 * A single message inside a thread.
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val sender: String,
    val content: String,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val attachments: List<String> = emptyList()
)
