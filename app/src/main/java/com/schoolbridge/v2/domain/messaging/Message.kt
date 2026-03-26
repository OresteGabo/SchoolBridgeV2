package com.schoolbridge.v2.domain.messaging

import java.util.UUID

/**
 * A single message inside a thread.
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val title: String? = null, // Optional title for the message card
    val sender: String, // E.g., "Finance Office", "Head Teacher"
    val content: String,
    val timestamp: String,
    var isUnread: Boolean = false,
    val actions: List<MessageAction> = emptyList(),
    val status: String? = null // E.g., "Confirmed", "Marked as paid"
)

data class MessageAction(
    val label: String,
    val actionId: String,
    val style: ActionStyle = ActionStyle.PRIMARY
)

enum class ActionStyle {
    PRIMARY,
    SECONDARY,
    OUTLINE
}
