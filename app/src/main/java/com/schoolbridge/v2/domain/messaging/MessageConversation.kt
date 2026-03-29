package com.schoolbridge.v2.domain.messaging

import java.util.UUID

/**
 * A topic-scoped interaction stream for school communication.
 *
 * This is intentionally not a free-chat model: each item can be read-only,
 * action-based, or conversational depending on [ConversationMode].
 */
data class MessageConversation(
    val id: String = UUID.randomUUID().toString(),
    val backendConversationId: Long? = null,
    val topic: String,
    val participantsLabel: String,
    val mode: ConversationMode = ConversationMode.ANNOUNCEMENT,
    val messages: MutableList<Message> = mutableListOf()
) {
    fun getUnreadCount(): Int = messages.count { it.isUnread }
    fun getLatestMessage(): Message? = messages.lastOrNull()
}

// Optional semantic alias for future UI copy.
typealias InteractionStream = MessageConversation
