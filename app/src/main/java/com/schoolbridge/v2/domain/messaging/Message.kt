package com.schoolbridge.v2.domain.messaging

import java.util.UUID

/**
 * A single message inside a thread.
 */
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val title: String? = null,
    val sender: String,
    val content: String,
    val timestamp: String,
    var isUnread: Boolean = false,
    val actions: List<MessageAction> = emptyList(),
    val status: String? = null,

    // --- NEW FIELDS FOR REPLY LOGIC ---
    val replyToId: String? = null,      // The ID of the message being replied to
    val replyToContent: String? = null, // The text snippet to show in the preview
    val replyToSender: String? = null   // The name of the person being replied to
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
