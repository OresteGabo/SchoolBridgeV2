package com.schoolbridge.v2.domain.messaging

import java.util.UUID

/**
 * A conversation that groups messages by subject and participants.
 * new audience = new thread
 * same audience + same topic = same thread
 */
data class MessageThread(
    val id: String = UUID.randomUUID().toString(),
    val topic: String,
    val participantsLabel: String, // E.g., "All Parents", "S4 Parents"
    val mode: ThreadMode = ThreadMode.ANNOUNCEMENT,
    val messages: MutableList<Message> = mutableListOf()
) {
    fun getUnreadCount(): Int = messages.count { it.isUnread }
    fun getLatestMessage(): Message? = messages.lastOrNull()
}
