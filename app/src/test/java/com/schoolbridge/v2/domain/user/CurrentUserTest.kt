package com.schoolbridge.v2.domain.user

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CurrentUserTest {

    @Test
    fun `role helpers reflect active and current roles`() {
        val user = CurrentUser(
            userId = "1",
            email = "teacher@example.com",
            firstName = "Alice",
            lastName = "Uwimana",
            activeRoles = setOf(UserRole.TEACHER, UserRole.PARENT),
            currentRole = UserRole.TEACHER
        )

        assertTrue(user.isTeacher())
        assertTrue(user.isParent())
        assertFalse(user.isStudent())
        assertFalse(user.isAdmin())

        assertTrue(user.isCurrentTeacher())
        assertFalse(user.isCurrentParent())
        assertFalse(user.isCurrentStudent())
        assertFalse(user.isCurrentAdmin())
    }

    @Test
    fun `current role helpers stay false when no current role is selected`() {
        val user = CurrentUser(
            userId = "2",
            email = "parent@example.com",
            firstName = "Beata",
            lastName = "Mukamana",
            activeRoles = setOf(UserRole.PARENT, UserRole.SCHOOL_ADMIN),
            currentRole = null
        )

        assertTrue(user.isParent())
        assertTrue(user.isAdmin())
        assertFalse(user.isCurrentParent())
        assertFalse(user.isCurrentAdmin())
        assertFalse(user.isCurrentTeacher())
        assertFalse(user.isCurrentStudent())
    }
}
