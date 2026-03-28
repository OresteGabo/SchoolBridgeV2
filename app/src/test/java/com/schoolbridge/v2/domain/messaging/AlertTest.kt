package com.schoolbridge.v2.domain.messaging

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDateTime

class AlertTest {

    @Test
    fun `source includes student and organization for school alerts when both are present`() {
        val alert = Alert(
            id = "a1",
            title = "Discipline follow-up",
            message = "Please review the note",
            timestamp = LocalDateTime.parse("2026-03-28T10:15:00"),
            type = AlertType.WARNING,
            publisherName = "Dean of Students",
            publisherType = AlertSourceType.SCHOOL,
            severity = AlertSeverity.HIGH,
            studentName = "Kevin Iradukunda",
            sourceOrganization = "Green Hills Academy"
        )

        assertEquals(
            "Dean of Students of Kevin Iradukunda at Green Hills Academy",
            alert.source
        )
    }

    @Test
    fun `source falls back to organization aware wording for non school publishers`() {
        val alert = Alert(
            id = "a2",
            title = "Maintenance notice",
            message = "Systems will be unavailable tonight",
            timestamp = LocalDateTime.parse("2026-03-28T11:15:00"),
            type = AlertType.NOTICE,
            publisherName = "SchoolBridge",
            publisherType = AlertSourceType.SYSTEM,
            severity = AlertSeverity.MEDIUM,
            sourceOrganization = "Platform operations"
        )

        assertEquals("SchoolBridge from Platform operations", alert.source)
    }

    @Test
    fun `source returns publisher name when no extra context exists`() {
        val alert = Alert(
            id = "a3",
            title = "Reminder",
            message = "Check your inbox",
            timestamp = LocalDateTime.parse("2026-03-28T12:15:00"),
            type = AlertType.REMINDER,
            publisherName = "SchoolBridge",
            publisherType = AlertSourceType.SYSTEM,
            severity = AlertSeverity.LOW
        )

        assertEquals("SchoolBridge", alert.source)
    }
}
