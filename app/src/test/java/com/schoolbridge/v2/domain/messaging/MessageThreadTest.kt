package com.schoolbridge.v2.domain.messaging

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MessageThreadTest {

    @Test
    fun `getUnreadCount counts only unread messages`() {
        val thread = MessageThread(
            topic = "Finance follow-up",
            participantsLabel = "Parent and bursar",
            messages = mutableListOf(
                Message(sender = "School", content = "Hello", timestamp = "09:00", isUnread = true),
                Message(sender = "Parent", content = "Thanks", timestamp = "09:05", isUnread = false),
                Message(sender = "School", content = "Reminder", timestamp = "09:10", isUnread = true)
            )
        )

        assertEquals(2, thread.getUnreadCount())
    }

    @Test
    fun `getLatestMessage returns last message in thread`() {
        val lastMessage = Message(sender = "Teacher", content = "See you tomorrow", timestamp = "11:15")
        val thread = MessageThread(
            topic = "Meeting",
            participantsLabel = "Teacher and parent",
            messages = mutableListOf(
                Message(sender = "Teacher", content = "Can we meet?", timestamp = "10:00"),
                lastMessage
            )
        )

        assertEquals(lastMessage, thread.getLatestMessage())
    }

    @Test
    fun `getLatestMessage returns null when thread is empty`() {
        val thread = MessageThread(
            topic = "Announcements",
            participantsLabel = "SchoolBridge"
        )

        assertNull(thread.getLatestMessage())
    }
}
