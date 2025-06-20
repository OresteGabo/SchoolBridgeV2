package com.schoolbridge.v2.domain.user

import android.util.Log
import com.schoolbridge.v2.domain.user.roles.Parent
import com.schoolbridge.v2.domain.user.roles.SchoolAdmin
import com.schoolbridge.v2.domain.user.roles.Student
import com.schoolbridge.v2.domain.user.roles.Teacher
import com.schoolbridge.v2.data.enums.UserVerificationStatus
import com.schoolbridge.v2.data.enums.VerificationMethod
import com.schoolbridge.v2.domain.academic.Enrollment
import kotlinx.serialization.Serializable

import java.time.LocalDate
import kotlin.random.Random

/**
 * Represents the core user profile on the client-side.
 * This is the central source of truth for the currently logged-in user's identity and capabilities.
 *
 * It consolidates general user information with specific details for each role the user might hold,
 * including identity verification status to manage trust levels and feature gating.
 *
 * Example:
 * ```
 * val user = getUserFromApi()
 * println("Welcome, ${user.firstName} ${user.lastName}!")
 * if (user.isStudent) {
 *     println("You are currently enrolled as a student.")
 *     println("Your grade level: ${user.studentDetails?.gradeLevel ?: "N/A"}")
 *     println("Courses: ${user.studentDetails?.currentSchoolLevelOffering?.courses?.joinToString { it.name }}")
 * }
 * ```
 *
 * @property id Unique identifier for the user (e.g., UUID from backend).
 * Example: `"usr_123e4567-e89b-12d3-a456-426614174000"`
 *
 * @property firstName The user's first name.
 * Example: `"Alice"`
 *
 * @property lastName The user's last name.
 * Example: `"Mukamana"`
 *
 * @property email The user's primary email address.
 * Example: `"alice.mukamana@example.com"`
 *
 * @property phoneNumber The user's phone number, if available.
 * Example: `"+250788123456"`
 *
 * @property gender The user's declared gender. Nullable if not specified.
 * Example: `Gender.FEMALE`
 *
 * @property dateOfBirth The user's date of birth. Nullable if not specified.
 * Example: `LocalDate.of(2004, 5, 17)`
 *
 * @property profilePictureUrl An optional URL to the user's profile picture.
 * Example: `"https://cdn.schoolbridge.com/profiles/usr_123e4567-e89b-12d3-a456-426614174000.jpg"`
 *
 * @property activeRoles A set of [UserRole] enums indicating all roles this user currently holds.
 * This is crucial for determining overall user capabilities.
 * Example: `setOf(UserRole.STUDENT, UserRole.PARENT)`
 *
 * @property verificationStatus The current verification status of the user's overall identity.
 * Indicates the trust level of the user's claimed identity.
 * Example: `UserVerificationStatus.VERIFIED`
 *
 * @property verificationMethodUsed The primary method used to verify the user's identity.
 * Example: `VerificationMethod.IN_PERSON_SCHOOL_ADMIN`
 *
 * @property verificationNotes Optional internal notes or reasons related to the user's identity verification.
 * Example: `"Verified by school admin on 2024-01-15"`
 *
 * @property verifiedByUserId The ID of the user who performed the verification for this profile,
 * if verification was delegated.
 * Example: `"usr_admin_001"`
 *
 * @property verifiedByUserMethod The method used by the delegating user to verify this user.
 * Example: `VerificationMethod.DELEGATED_BY_VERIFIED_PARENT`
 *
 * @property studentDetails An optional instance of [Student] data if the user has the student role.
 * Provides student-specific profile information such as current enrollment and school level offering.
 *
 * Example usage:
 * ```
 * user.studentDetails?.let { student ->
 *     println("Student grade level: ${student.gradeLevel}")
 *     println("Currently enrolled in: ${student.currentEnrollment.academicYearId}")
 * }
 * ```
 *
 * @property teacherDetails An optional instance of [Teacher] data if the user has the teacher role.
 * Provides teacher-specific profile information such as assignments and subjects taught.
 *
 * Example usage:
 * ```
 * user.teacherDetails?.let { teacher ->
 *     println("Assigned courses: ${teacher.assignedCourses.joinToString { it.name }}")
 * }
 * ```
 *
 * @property parentDetails An optional instance of [Parent] data if the user has the parent role.
 * Contains linked children and parental access info.
 *
 * Example usage:
 * ```
 * user.parentDetails?.linkedChildren?.forEach { child ->
 *     println("Child: ${child.firstName} ${child.lastName}")
 * }
 * ```
 *
 * @property schoolAdminDetails An optional instance of [SchoolAdmin] data if the user has a school administrator role.
 * Includes admin-specific roles and permissions.
 *
 * Example usage:
 * ```
 * user.schoolAdminDetails?.currentRole?.let { role ->
 *     println("Admin role: ${role.title}")
 * }
 * ```
 */
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val gender: Gender?,
    val dateOfBirth: LocalDate?,
    val profilePictureUrl: String?,
    val activeRoles: Set<UserRole>,

    // Identity verification details
    val verificationStatus: UserVerificationStatus,
    val verificationMethodUsed: VerificationMethod?,
    val verificationNotes: String?,
    val verifiedByUserId: String?,
    val verifiedByUserMethod: VerificationMethod?,

    // Role-specific data objects (only non-null if applicable)
    val studentDetails: Student? = null,
    val teacherDetails: Teacher? = null,
    val parentDetails: Parent? = null,
    val schoolAdminDetails: SchoolAdmin? = null
)
{
    /**
     * Returns true if the user currently has the STUDENT role.
     */
    val isStudent: Boolean get() = activeRoles.contains(UserRole.STUDENT)

    /**
     * Returns true if the user currently has the TEACHER role.
     */
    val isTeacher: Boolean get() = activeRoles.contains(UserRole.TEACHER)

    /**
     * Returns true if the user currently has the PARENT role.
     */
    val isParent: Boolean get() = activeRoles.contains(UserRole.PARENT)

    /**
     * Returns true if the user currently has the SCHOOL_ADMIN role.
     */
    val isAdmin: Boolean get() = activeRoles.contains(UserRole.SCHOOL_ADMIN)
}





data class CurrentUser(
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val activeRoles: List<String>,
    val phoneNumber: String?,
    val nationalId: String?,
    val address: Address?, // Nested data class
    val profilePictureUrl: String?,
    val role: String?, // Assuming a primary role for the user
    val joinDate: String?,
    val linkedStudents: List<LinkedStudent>?, // Nested data class
    val gender: Gender?, // Make sure gender is directly passed and not hardcoded to null
    val isVerified: Boolean, // existing property

) {
    @Serializable
    data class Address(
        val district: String?,
        val sector: String?,
        val cell: String?,
        val village: String?
    )

    @Serializable
    data class LinkedStudent(
        val id: String,
        val firstName: String,
        val lastName: String
    )

    fun isParent(): Boolean {
        return activeRoles.contains("PARENT")
    }

    // This method can be kept or removed since you now have a direct property
    fun isStudent(): Boolean {
        return activeRoles.contains("STUDENT")
    }
}

