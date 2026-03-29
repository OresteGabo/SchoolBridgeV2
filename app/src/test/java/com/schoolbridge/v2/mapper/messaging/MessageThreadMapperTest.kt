package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.MessageConversationDto
import com.schoolbridge.v2.data.dto.messaging.MessageInConversationDto
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.ConversationMode
import com.schoolbridge.v2.domain.messaging.MessageConversation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MessageThreadMapperTest {

    @Test
    fun `thread dto toDomain maps participants mode and messages`() {
        val dto = MessageConversationDto(
            id = "thread-1",
            subject = "Role verification",
            participants = listOf("School admin", "Teacher candidate"),
            createdAt = "2026-03-28T08:00:00Z",
            lastMessageAt = "2026-03-28T08:30:00Z",
            threadType = "action_required",
            relatedEntityId = "role-7",
            relatedEntityType = "ROLE_REQUEST",
            isArchived = false,
            autoDeleteDate = null,
            messages = listOf(
                MessageInConversationDto(
                    id = "msg-1",
                    senderId = "admin-1",
                    content = "Please upload your degree.",
                    sentAt = "2026-03-28T08:10:00Z",
                    readBy = null,
                    inAppAttachments = null
                )
            )
        )

        val thread = dto.toDomain()

        assertEquals("thread-1", thread.id)
        assertEquals("Role verification", thread.topic)
        assertEquals("School admin, Teacher candidate", thread.participantsLabel)
        assertEquals(ConversationMode.ACTION_REQUIRED, thread.mode)
        assertEquals(1, thread.messages.size)
        assertEquals("Please upload your degree.", thread.messages.first().content)
    }

    @Test
    fun `thread dto toDomain falls back to announcement for unknown mode`() {
        val dto = MessageConversationDto(
            id = "thread-2",
            subject = "General notice",
            participants = emptyList(),
            createdAt = "2026-03-28T08:00:00Z",
            lastMessageAt = "2026-03-28T08:30:00Z",
            threadType = "UNKNOWN_MODE",
            relatedEntityId = null,
            relatedEntityType = null,
            isArchived = false,
            autoDeleteDate = null,
            messages = emptyList()
        )

        val thread = dto.toDomain()

        assertEquals(ConversationMode.ANNOUNCEMENT, thread.mode)
    }

    @Test
    fun `thread toDto splits trimmed participants and maps message list`() {
        val thread = MessageConversation(
            id = "thread-3",
            topic = "Parent meeting",
            participantsLabel = "Teacher A, Parent B,  Counselor C ",
            mode = ConversationMode.CONVERSATION,
            messages = mutableListOf(
                Message(
                    id = "msg-2",
                    sender = "Teacher A",
                    content = "Can we meet on Tuesday?",
                    timestamp = "2026-03-28T13:00:00Z"
                )
            )
        )

        val dto = thread.toDto()

        assertEquals("Parent meeting", dto.subject)
        assertEquals(listOf("Teacher A", "Parent B", "Counselor C"), dto.participants)
        assertEquals("CONVERSATION", dto.threadType)
        assertEquals(1, dto.messages.size)
        assertEquals("Can we meet on Tuesday?", dto.messages.first().content)
        assertTrue(dto.createdAt.isNotBlank())
        assertTrue(dto.lastMessageAt.isNotBlank())
    }
}
