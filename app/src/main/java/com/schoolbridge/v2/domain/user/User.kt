package com.schoolbridge.v2.domain.user

import com.schoolbridge.v2.domain.user.roles.Parent
import com.schoolbridge.v2.domain.user.roles.SchoolAdmin
import com.schoolbridge.v2.domain.user.roles.Student
import com.schoolbridge.v2.domain.user.roles.Teacher
import java.time.LocalDate // Assuming LocalDate is used for dates
import com.schoolbridge.v2.data.enums.UserVerificationStatus // Import the new enum
import com.schoolbridge.v2.data.enums.VerificationMethod // Import the new enum
import kotlinx.serialization.Serializable

/**
 * Represents the core user profile on the client-side.
 * This is the central source of truth for the currently logged-in user's identity and capabilities.
 *
 * It consolidates general user information with specific details for each role the user might hold,
 * crucially including the user's identity verification status.
 *
 * @property id Unique identifier for the user (e.g., UUID from backend).
 * @property firstName The user's first name.
 * @property lastName The user's last name.
 * @property email The user's primary email address.
 * @property phoneNumber The user's phone number, if available.
 * @property gender The user's declared gender.
 * @property dateOfBirth The user's date of birth.
 * @property profilePictureUrl An optional URL to the user's profile picture.
 * @property activeRoles A set of [UserRole] enums indicating all roles this user currently holds.
 * This is crucial for determining overall user capabilities (e.g., if `UserRole.STUDENT` is present, `isStudent` is true).
 *
 * @property verificationStatus The current verification status of the user's overall identity.
 * This indicates the level of trust in the user's claimed identity (e.g., [UserVerificationStatus.VERIFIED],
 * [UserVerificationStatus.PENDING_REVIEW]). This is vital for UI display and feature gating.
 * @property verificationMethodUsed The primary method used to verify the user's identity (e.g.,
 * [VerificationMethod.IN_PERSON_SCHOOL_ADMIN], [VerificationMethod.MOBILE_MONEY_NAME_MATCH],
 * [VerificationMethod.DELEGATED_BY_VERIFIED_USER]). Null if the user's identity is not yet verified.
 * @property verificationNotes Optional internal notes or reasons related to the user's identity
 * verification status (e.g., "Re-verification initiated due to fraudulent source user").
 * @property verifiedByUserId The ID of the user who performed the verification for this profile,
 * if this user's identity was delegated by another user (e.g., a school admin or another verified parent).
 * Null if verified through an automated process or a direct, non-delegated method.
 * @property verifiedByUserMethod The method used by the `verifiedByUserId` to perform the delegation.
 * This specifies how the delegation itself was conducted (e.g., [VerificationMethod.DELEGATED_BY_VERIFIED_PARENT]).
 * Null if `verifiedByUserId` is null.
 *
 * @property studentDetails An optional instance of [Student] data if the user is a student.
 * - **Purpose:** To provide specific student-related information like current enrollment, grade level, or linked parents, directly accessible from the user object.
 * - **Data Example:** If this is non-null, you can access `studentDetails.currentEnrollment.academicYearId` to see the student's current academic year, or `studentDetails.gradeLevel` to display their class.
 * - **Necessity:** Allows the UI to easily display student-specific dashboards or details without needing separate API calls or manual lookups for the authenticated user's student profile. It's `null` if the user is not a student.
 *
 * @property teacherDetails An optional instance of [Teacher] data if the user is a teacher.
 * - **Purpose:** To provide teacher-specific information like assigned courses, faculty, or years of experience, directly accessible from the user object.
 * - **Data Example:** If this is non-null, you can access `teacherDetails.assignedCourses.first().name` to show one of the courses the teacher is assigned to, or `teacherDetails.qualification` for their background.
 * - **Necessity:** Simplifies displaying a teacher's schedule, assigned subjects, or professional profile. It's `null` if the user is not a teacher.
 *
 * @property parentDetails An optional instance of [Parent] data if the user is a parent.
 * - **Purpose:** To provide parent-specific information, primarily about their linked children.
 * - **Data Example:** If this is non-null, you can access `parentDetails.linkedChildren.size` to see how many children are linked, or `parentDetails.linkedChildren.first().studentFirstName` to display a child's name.
 * - **Necessity:** Enables the "My Children" section of a parent's dashboard, displaying their children's summary information. It's `null` if the user is not a parent.
 *
 * @property schoolAdminDetails An optional instance of [SchoolAdmin] data if the user is a school administrator.
 * - **Purpose:** To provide admin-specific details like their administrative title or the school they manage.
 * - **Data Example:** If this is non-null, you can access `schoolAdminDetails.adminRoleTitle` to show "Principal" or "Registrar".
 * - **Necessity:** Useful for displaying an admin's specific responsibilities or school context on their profile screen. It's `null` if the user is not an administrator.
 */
data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phoneNumber: String?,
    val gender: Gender?,
    val dateOfBirth: LocalDate?, // Using LocalDate for type safety
    val profilePictureUrl: String?,
    val activeRoles: Set<UserRole>,

    // NEW FIELDS FOR IDENTITY VERIFICATION
    val verificationStatus: UserVerificationStatus,
    val verificationMethodUsed: VerificationMethod?,
    val verificationNotes: String?,
    val verifiedByUserId: String?,
    val verifiedByUserMethod: VerificationMethod?,

    // Role-specific details (only non-null if the user has that role)
    val studentDetails: Student? = null,
    val teacherDetails: Teacher? = null,
    val parentDetails: Parent? = null,
    val schoolAdminDetails: SchoolAdmin? = null
) {
    /**
     * Convenience getter to quickly check if the user is a student.
     */
    val isStudent: Boolean get() = activeRoles.contains(UserRole.STUDENT)

    /**
     * Convenience getter to quickly check if the user is a teacher.
     */
    val isTeacher: Boolean get() = activeRoles.contains(UserRole.TEACHER)

    /**
     * Convenience getter to quickly check if the user is a parent.
     */
    val isParent: Boolean get() = activeRoles.contains(UserRole.PARENT)

    /**
     * Convenience getter to quickly check if the user is a school administrator.
     */
    val isAdmin: Boolean get() = activeRoles.contains(UserRole.SCHOOL_ADMIN)

    // Example Usage in UI/ViewModel:
    /*
    fun displayUserSpecificContent(user: User) {
        println("Welcome, ${user.firstName} ${user.lastName}!")

        // Display user's identity verification status prominently
        println("Your profile status: ${user.verificationStatus.name}")
        if (user.verificationStatus == UserVerificationStatus.VERIFIED) {
            println("Verified via: ${user.verificationMethodUsed?.name ?: "Unknown"}")
            user.verifiedByUserId?.let {
                println("Delegated by user ID: $it (Method: ${user.verifiedByUserMethod?.name ?: "N/A"})")
            }
        } else if (user.verificationStatus == UserVerificationStatus.PENDING_REVIEW) {
            println("Please check your email for updates on your verification.")
        }

        if (user.isStudent && user.studentDetails != null) {
            println("Your current grade level: ${user.studentDetails.gradeLevel}")
            // Enable student dashboard features
        }

        if (user.isTeacher && user.teacherDetails != null) {
            println("Courses you teach: ${user.teacherDetails.assignedCourses.joinToString { it.name }}")
            // Enable teacher grading tools
        }

        if (user.isParent && user.parentDetails != null) {
            println("Children linked to your account: ${user.parentDetails.linkedChildren.joinToString { it.studentFirstName }}")
            // Show child progress reports, including linkage verification status for each child
        }

        if (user.isAdmin && user.schoolAdminDetails != null) {
            println("Your admin role: ${user.schoolAdminDetails.adminRoleTitle}")
            // Show admin management panel
        }
    }
    */
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
    val isVerified: Boolean // <--- ADDED THIS PROPERTY
) {
    @Serializable // Keep if you use it for other serialization, otherwise remove if only Gson is used here
    data class Address(
        val district: String?,
        val sector: String?,
        val cell: String?,
        val village: String?
    )

    @Serializable // Keep if you use it for other serialization, otherwise remove if only Gson is used here
    data class LinkedStudent(
        val id: String,
        val firstName: String,
        val lastName: String
    )

    fun isParent(): Boolean {
        return activeRoles.contains("parent")
    }
    fun isStudent(): Boolean {
        return activeRoles.contains("student")
    }
}