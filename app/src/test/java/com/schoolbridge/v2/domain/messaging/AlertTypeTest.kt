package com.schoolbridge.v2.domain.messaging

import org.junit.Assert.assertEquals
import org.junit.Test

class AlertTypeTest {

    @Test
    fun `prettyName returns human friendly labels`() {
        assertEquals("Announcement", AlertType.ANNOUNCEMENT.prettyName())
        assertEquals("Reminder", AlertType.REMINDER.prettyName())
        assertEquals("Warning", AlertType.WARNING.prettyName())
        assertEquals("Notice", AlertType.NOTICE.prettyName())
        assertEquals("Error", AlertType.ERROR.prettyName())
        assertEquals("Info", AlertType.INFO.prettyName())
        assertEquals("Success", AlertType.SUCCESS.prettyName())
    }
}
