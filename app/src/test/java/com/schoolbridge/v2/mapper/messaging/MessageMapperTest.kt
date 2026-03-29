package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.MessageInConversationDto
import com.schoolbridge.v2.domain.messaging.Message
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageMapperTest {

    @Test
    fun `message dto toDomain keeps essential message fields`() {
        val dto = MessageInConversationDto(
            id = "msg-1",
            senderId = "teacher-9",
            content = "Please come with the signed form tomorrow.",
            sentAt = "2026-03-28T08:30:00Z",
            readBy = listOf("parent-1"),
            inAppAttachments = null
        )

        val message = dto.toDomain()

        assertEquals("msg-1", message.id)
        assertEquals("teacher-9", message.sender)
        assertEquals("Please come with the signed form tomorrow.", message.content)
        assertEquals("2026-03-28T08:30:00Z", message.timestamp)
        assertFalse(message.isUnread)
        assertFalse(message.isFromCurrentUser)
        assertTrue(message.actions.isEmpty())
    }

    @Test
    fun `message toDto keeps sender and content fields`() {
        val message = Message(
            id = "msg-2",
            sender = "School Admin",
            content = "Documents received.",
            timestamp = "2026-03-28T09:45:00Z"
        )

        val dto = message.toDto()

        assertEquals("msg-2", dto.id)
        assertEquals("School Admin", dto.senderId)
        assertEquals("Documents received.", dto.content)
        assertEquals("2026-03-28T09:45:00Z", dto.sentAt)
        assertEquals(emptyList<String>(), dto.readBy)
    }
}
