package com.schoolbridge.v2.domain.messaging

import org.junit.Assert.assertEquals
import org.junit.Test

class RecipientTypeTest {

    @Test
    fun `fromRaw matches known values case insensitively`() {
        assertEquals(RecipientType.ALL_PARENTS, RecipientType.fromRaw("ALL_PARENTS"))
        assertEquals(RecipientType.ALL_STUDENTS, RecipientType.fromRaw("all_students"))
        assertEquals(RecipientType.INDIVIDUAL, RecipientType.fromRaw("Individual"))
    }

    @Test
    fun `fromRaw falls back to all users for null or unknown values`() {
        assertEquals(RecipientType.ALL_USERS, RecipientType.fromRaw(null))
        assertEquals(RecipientType.ALL_USERS, RecipientType.fromRaw("UNKNOWN_AUDIENCE"))
    }
}
