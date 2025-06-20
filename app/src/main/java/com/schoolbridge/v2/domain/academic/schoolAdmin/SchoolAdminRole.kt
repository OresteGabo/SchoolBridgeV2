package com.schoolbridge.v2.domain.academic.schoolAdmin

import java.time.LocalDate

/**
 * Represents a period during which a school admin holds a specific role.
 *
 * @property roleType The type of administrative role.
 * @property assignedSince The date the admin was assigned this role.
 * @property assignedUntil The optional end date of this role assignment. Null if ongoing.
 */
data class SchoolAdminRole(
    val roleType: SchoolAdminRoleType,
    val assignedSince: LocalDate,
    val assignedUntil: LocalDate? = null
) {
    /**
     * Returns true if this role is currently active (no end date or end date is in the future).
     */
    val isCurrent: Boolean
        get() = assignedUntil == null || assignedUntil.isAfter(LocalDate.now())
}