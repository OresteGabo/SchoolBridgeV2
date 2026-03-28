package com.schoolbridge.v2.domain.user

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class PunishmentTypeTest {

    @Test
    fun `fromString matches values case insensitively`() {
        assertEquals(PunishmentType.WARNING, PunishmentType.fromString("warning"))
        assertEquals(PunishmentType.DETENTION, PunishmentType.fromString("DETENTION"))
        assertEquals(PunishmentType.SUSPENSION, PunishmentType.fromString("Suspension"))
    }

    @Test
    fun `fromString throws for unknown values`() {
        try {
            PunishmentType.fromString("COMMUNITY_SERVICE")
            fail("Expected IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals("Unknown PunishmentType: COMMUNITY_SERVICE", exception.message)
        }
    }
}
