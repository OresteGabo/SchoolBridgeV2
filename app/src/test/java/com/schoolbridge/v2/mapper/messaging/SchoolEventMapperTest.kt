package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.SchoolEventDto
import com.schoolbridge.v2.domain.messaging.RecipientType
import com.schoolbridge.v2.domain.messaging.SchoolEvent
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SchoolEventMapperTest {

    @Test
    fun `toDomain maps dto values into school event`() {
        val dto = SchoolEventDto(
            id = "event-1",
            schoolId = "school-5",
            title = "Parent meeting",
            description = "Discuss end of term progress",
            type = "MEETING",
            startDate = "2026-04-02T15:00:00Z",
            endDate = "2026-04-02T16:00:00Z",
            location = "Hall A",
            targetAudience = "ALL_PARENTS",
            organizerUserId = "admin-3",
            inAppAttachments = null
        )

        val event = dto.toDomain()

        assertEquals("event-1", event.id)
        assertEquals("school-5", event.schoolId)
        assertEquals("Parent meeting", event.title)
        assertEquals("MEETING", event.type)
        assertEquals(RecipientType.ALL_PARENTS, event.recipientType)
        assertTrue(event.attachments.isEmpty())
    }

    @Test
    fun `toDomain falls back to all users when target audience is missing`() {
        val dto = SchoolEventDto(
            id = "event-2",
            schoolId = "school-5",
            title = "Open briefing",
            description = "Platform changes",
            type = "ANNOUNCEMENT",
            startDate = "2026-04-03T09:00:00Z",
            endDate = "2026-04-03T10:00:00Z",
            location = "Main court",
            targetAudience = null,
            organizerUserId = "system",
            inAppAttachments = null
        )

        val event = dto.toDomain()

        assertEquals(RecipientType.ALL_USERS, event.recipientType)
    }

    @Test
    fun `toDto keeps empty attachments nullable`() {
        val dto = SchoolEvent(
            id = "event-3",
            schoolId = "school-7",
            title = "Science fair",
            description = "Student showcase",
            type = "ACADEMIC",
            startDate = "2026-04-10T08:00:00Z",
            endDate = "2026-04-10T15:00:00Z",
            location = "Lab block",
            recipientType = RecipientType.ALL_STUDENTS,
            organizerUserId = "teacher-4",
            attachments = emptyList()
        ).toDto()

        assertEquals("ALL_STUDENTS", dto.targetAudience)
        assertNull(dto.inAppAttachments)
    }
}
