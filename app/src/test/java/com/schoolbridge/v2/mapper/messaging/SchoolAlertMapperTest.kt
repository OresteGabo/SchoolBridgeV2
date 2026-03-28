package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.SchoolAlertDto
import com.schoolbridge.v2.domain.messaging.AlertType
import com.schoolbridge.v2.domain.messaging.RecipientType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SchoolAlertMapperTest {

    @Test
    fun `toDomain maps raw dto fields into domain model`() {
        val dto = SchoolAlertDto(
            id = "alert-1",
            schoolId = "school-1",
            createdByUserId = "admin-9",
            targetAudience = "ALL_PARENTS",
            type = "warning",
            title = "Bus route update",
            content = "Pickup time changed",
            publishedDate = "2026-03-28T10:30:00Z",
            expiryDate = "2026-03-29T10:30:00Z",
            inAppAttachments = null
        )

        val domain = dto.toDomain()

        assertEquals("alert-1", domain.id)
        assertEquals("school-1", domain.schoolId)
        assertEquals("admin-9", domain.createdByUserId)
        assertEquals(RecipientType.ALL_PARENTS, domain.recipientType)
        assertEquals(AlertType.WARNING, domain.type)
        assertEquals("Bus route update", domain.title)
        assertEquals("Pickup time changed", domain.content)
        assertEquals("2026-03-28T10:30:00Z", domain.publishedAt)
        assertEquals("2026-03-29T10:30:00Z", domain.expiresAt)
        assertTrue(domain.attachments.isEmpty())
    }

    @Test
    fun `toDomain falls back to safe defaults for unknown audience and type`() {
        val dto = SchoolAlertDto(
            id = "alert-2",
            schoolId = "school-2",
            createdByUserId = "system",
            targetAudience = "UNKNOWN_GROUP",
            type = "something-new",
            title = "Platform note",
            content = "We are updating services",
            publishedDate = "2026-03-28T12:00:00Z",
            expiryDate = null,
            inAppAttachments = null
        )

        val domain = dto.toDomain()

        assertEquals(RecipientType.ALL_USERS, domain.recipientType)
        assertEquals(AlertType.ANNOUNCEMENT, domain.type)
        assertNull(domain.expiresAt)
    }

    @Test
    fun `toDto keeps empty attachments nullable for cleaner payloads`() {
        val dto = com.schoolbridge.v2.domain.messaging.SchoolAlert(
            id = "alert-3",
            schoolId = "school-3",
            createdByUserId = "admin-2",
            recipientType = RecipientType.ALL_STAFF,
            type = AlertType.NOTICE,
            title = "Staff memo",
            content = "Please sign the register",
            publishedAt = "2026-03-28T14:00:00Z",
            expiresAt = null,
            attachments = emptyList()
        ).toDto()

        assertEquals("ALL_STAFF", dto.targetAudience)
        assertEquals("NOTICE", dto.type)
        assertNull(dto.inAppAttachments)
    }
}
