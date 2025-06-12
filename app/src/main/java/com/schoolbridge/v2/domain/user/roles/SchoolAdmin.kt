package com.schoolbridge.v2.domain.user.roles

/**
 * Client-side domain model representing a School Administrator's specific profile data.
 * This class holds data directly relevant to a user when they act as a school administrator.
 * It does NOT contain methods for administrative actions (e.g., managing enrollments),
 * as those are handled by backend API calls.
 *
 * @property userId The ID of the main [User] object this administrator profile belongs to.
 * @property adminId A unique identifier for this administrator profile, which might be the same as [userId]
 * or a distinct school-generated admin ID.
 * @property adminRoleTitle The specific title of the administrator's role (e.g., "Principal", "Registrar", "Head of Admissions").
 * Used for display in their profile or across administrative dashboards.
 * @property managesSchoolId The ID of the specific school that this administrator's role is primarily associated with.
 * Useful for multi-school systems where an admin might manage a particular branch.
 *
 * Example Usage:
 * val adminData = user.schoolAdminDetails // Assuming user is an admin
 * if (adminData != null) {
 * println("Admin ${adminData.userId} is the ${adminData.adminRoleTitle} for school ${adminData.managesSchoolId}.")
 * // Enable admin-specific UI options based on their role title
 * }
 */
data class SchoolAdmin(
    val userId: String,
    val adminId: String,
    val adminRoleTitle: String,
    val managesSchoolId: String
)