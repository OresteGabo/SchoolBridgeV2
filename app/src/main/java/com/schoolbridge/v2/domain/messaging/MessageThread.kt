package com.schoolbridge.v2.domain.messaging


import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDateTime
import java.util.UUID

/**
 * A conversation that groups messages by subject and participants.
 */
/*
data class MessageThread(
    val id: String,
    val subject: String,
    val participants: List<String>, // Names or emails
    val messages: List<Message>,
    val lastSnippet: String,
    val lastDate: LocalDateTime,
    val isSystem: Boolean = false
) {
    val unreadCount: Int
        get() = messages.count { !it.isRead }

    val isUnread: Boolean
        get() = unreadCount > 0

    val lastMessage: Message?
        get() = messages.maxByOrNull { it.timestamp }
}*/
data class MessageThread(
    val topic: String,
    val messages: MutableList<Message> = mutableListOf()
) {
    fun getUnreadCount(): Int = messages.count { it.isUnread }
    fun getLatestMessage(): Message? = messages.lastOrNull()
}

/**
 * A pending invitation to join a thread.
 */
data class ThreadInvite(
    val id: String,
    val subject: String,
    val from: String,
    val receivedAt: LocalDateTime
)


