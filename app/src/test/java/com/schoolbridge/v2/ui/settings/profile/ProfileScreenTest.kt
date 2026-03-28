package com.schoolbridge.v2.ui.settings.profile

import org.junit.Assert.assertEquals
import org.junit.Test

class ProfileScreenTest {

    @Test
    fun `maskRwandaId keeps first section and last digits for long ids`() {
        val masked = maskRwandaId("12345678901234567")

        assertEquals("1 2345 •••••••• 567", masked)
    }

    @Test
    fun `maskRwandaId leaves short values unchanged`() {
        val masked = maskRwandaId("12345")

        assertEquals("12345", masked)
    }

    @Test
    fun `maskRwandaId ignores formatting when counting digits but preserves leading character`() {
        val masked = maskRwandaId("1 2345 6789 0123 4567")

        assertEquals("1 2345 •••••••• 567", masked)
    }
}
