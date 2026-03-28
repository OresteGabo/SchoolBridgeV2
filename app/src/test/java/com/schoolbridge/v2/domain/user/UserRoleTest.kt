package com.schoolbridge.v2.domain.user

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class UserRoleTest {

    @Test
    fun `fromBackendValue returns matching role`() {
        assertEquals(UserRole.SCHOOL_ADMIN, UserRole.fromBackendValue("SCHOOL_ADMIN"))
        assertEquals(UserRole.TEACHER, UserRole.fromBackendValue("TEACHER"))
    }

    @Test
    fun `fromBackendValue throws for unknown value`() {
        try {
            UserRole.fromBackendValue("UNKNOWN_ROLE")
            fail("Expected IllegalArgumentException")
        } catch (exception: IllegalArgumentException) {
            assertEquals("Unknown UserRole backend value: UNKNOWN_ROLE", exception.message)
        }
    }
}
