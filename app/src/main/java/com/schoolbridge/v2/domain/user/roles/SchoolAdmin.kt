package com.schoolbridge.v2.domain.user.roles

import com.schoolbridge.v2.domain.academic.schoolAdmin.SchoolAdminRole


/**
 * Represents school admin-specific details including all roles held over time.
 *
 * @property adminId The unique ID of the admin user.
 * @property roles List of all [SchoolAdminRole]s representing each administrative role period.
 */
data class SchoolAdmin(
    val adminId: String,
    val roles: List<SchoolAdminRole>
) {
    /**
     * The currently active role, or null if none.
     */
    val currentRole: SchoolAdminRole?
        get() = roles.find { it.isCurrent }
}
