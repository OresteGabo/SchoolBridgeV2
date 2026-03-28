package com.schoolbridge.v2.mapper.messaging

import com.schoolbridge.v2.data.dto.messaging.AlertDto
import com.schoolbridge.v2.domain.messaging.AlertSeverity
import com.schoolbridge.v2.domain.messaging.AlertSourceType
import com.schoolbridge.v2.domain.messaging.AlertType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime

class AlertMapperTest {

    @Test
    fun `toDomain maps valid dto content and date`() {
        val dto = AlertDto(
            id = "alert-1",
            userId = "user-1",
            type = "warning",
            title = "Fee reminder",
            content = "Your balance is due tomorrow.",
            sentDate = "2026-03-28T13:15:00",
            isRead = true,
            linkToEntityId = "invoice-9",
            linkToEntityType = "INVOICE",
            expiryDate = null
        )

        val alert = dto.toDomain()

        assertEquals("alert-1", alert.id)
        assertEquals("Fee reminder", alert.title)
        assertEquals("Your balance is due tomorrow.", alert.message)
        assertEquals(LocalDateTime.parse("2026-03-28T13:15:00"), alert.timestamp)
        assertEquals(AlertType.WARNING, alert.type)
        assertEquals(true, alert.isRead)
        assertEquals("SchoolBridge", alert.publisherName)
        assertEquals(AlertSourceType.SYSTEM, alert.publisherType)
        assertEquals(AlertSeverity.MEDIUM, alert.severity)
    }

    @Test
    fun `toDomain falls back to info and current time for bad payload`() {
        val before = LocalDateTime.now().minusSeconds(1)
        val dto = AlertDto(
            id = "alert-2",
            userId = "user-2",
            type = "brand_new_type",
            title = "Update",
            content = "Please refresh your page.",
            sentDate = "not-a-date",
            isRead = false,
            linkToEntityId = null,
            linkToEntityType = null,
            expiryDate = null
        )

        val alert = dto.toDomain()
        val after = LocalDateTime.now().plusSeconds(1)

        assertEquals(AlertType.INFO, alert.type)
        assertTrue(!alert.timestamp.isBefore(before) && !alert.timestamp.isAfter(after))
    }
}
