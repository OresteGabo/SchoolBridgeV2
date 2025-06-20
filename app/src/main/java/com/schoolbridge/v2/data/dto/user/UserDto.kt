package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.common.ContentAttachmentDto
import com.schoolbridge.v2.data.dto.user.student.StudentDetailsDto
import com.schoolbridge.v2.data.enums.UserVerificationStatus
import com.schoolbridge.v2.data.enums.VerificationMethod

/**
 * Data Transfer Object (DTO) for a **full User profile** as received from the API.
 *
 * This DTO directly mirrors the complete JSON structure for a user, encompassing their
 * general attributes (name, email, phone), their identity verification status, and any
 * role-specific details (student, teacher, parent, admin) as nested DTOs. It serves as the
 * comprehensive data model for a user's identity and roles within the system.
 *
 * **Real-life Example:**
 * After a successful login, the application might fetch the user's full profile using a GET
 * request to `/users/me`. The response would be deserialized into this [UserDto] to populate
 * the user's dashboard, profile screen, and determine their permissions based on `activeRoles`
 * and specific details, as well as indicating the trustworthiness of their identity.
 *
 * @property id A unique identifier for the user. Example: "usr_abc123"
 * @property firstName The user's first name. Example: "Jean"
 * @property lastName The user's last name. Example: "Ndayisaba"
 * @property email The user's primary email address. Example: "jean.ndayisaba@example.com"
 * @property phoneNumber The user's phone number, nullable if not provided. Example: "+250788123456"
 * @property gender The user's gender, represented as a string (e.g., "MALE", "FEMALE", "OTHER").
 * Nullable if not provided.
 * @property dateOfBirth The user's date of birth as an ISO 8601 date string (e.g., "YYYY-MM-DD").
 * Nullable if not provided. Example: "2000-01-15"
 * @property profilePictureUrl An optional URL to the user's profile picture.
 * Example: "https://cdn.schoolvridge.com/profiles/usr_abc123.jpg"
 * @property activeRoles A list of strings representing the roles currently active for this user
 * (e.g., `["STUDENT", "PARENT"]`, `["TEACHER"]`, `["SCHOOL_ADMIN"]`). This determines which
 * role-specific details ([studentDetails], [teacherDetails], etc.) are present and relevant.
 * @property verificationStatus The current verification status of the user's overall identity.
 * This indicates the level of trust in the user's claimed identity (e.g., `VERIFIED`, `PENDING_REVIEW`).
 * @property verificationMethodUsed The primary method used to verify the user's identity (e.g.,
 * `IN_PERSON_SCHOOL_ADMIN`, `MOBILE_MONEY_NAME_MATCH`, `DELEGATED_BY_VERIFIED_USER`). Null if
 * the user's identity is not yet verified.
 * @property verificationNotes Optional internal notes or reasons related to the user's identity
 * verification status (e.g., "Re-verified due to fraudulent source user").
 * @property verifiedByUserId The ID of the user who performed the verification for this profile,
 * if the verification was delegated by another user (e.g., a school admin or another verified parent).
 * Null if verified through an automated process or a direct, non-delegated method.
 * @property verifiedByUserMethod The method used by the `verifiedByUserId` to perform the delegation.
 * This specifies how the delegation itself was conducted (e.g., 'DELEGATED_BY_VERIFIED_PARENT').
 * Null if `verifiedByUserId` is null.
 * @property studentDetails Nested DTO for student-specific information. This will be non-null
 * only if the user has the "STUDENT" role.
 * @property teacherDetails Nested DTO for teacher-specific information. This will be non-null
 * only if the user has the "TEACHER" role.
 * @property parentDetails Nested DTO for parent-specific information. This will be non-null
 * only if the user has the "PARENT" role.
 * @property schoolAdminDetails Nested DTO for school administrator-specific information. This will
 * be non-null only if the user has the "SCHOOL_ADMIN" role.
 * @property attachments A list of [ContentAttachmentDto] objects containing embedded content
 * directly associated with this user's profile (e.g., a profile picture as Base64, a biography). Nullable.
 */
data class UserDto(
    @SerializedName("id") val id: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("gender") val gender: String?, // Or an enum for Gender
    @SerializedName("date_of_birth") val dateOfBirth: String?, // ISO 8601 date string
    @SerializedName("profile_picture_url") val profilePictureUrl: String?,
    @SerializedName("active_roles") val activeRoles: List<String>,

    // Verification fields for the user's own identity
    @SerializedName("verification_status") val verificationStatus: UserVerificationStatus,
    @SerializedName("verification_method_used") val verificationMethodUsed: VerificationMethod?,
    @SerializedName("verification_notes") val verificationNotes: String?,

    // Fields for delegated verification (who verified this user's profile)
    @SerializedName("verified_by_user_id") val verifiedByUserId: String?,
    @SerializedName("verified_by_user_method") val verifiedByUserMethod: VerificationMethod?,

    // Nested DTOs for role-specific details
    @SerializedName("student_details") val studentDetails: StudentDetailsDto?,
    @SerializedName("teacher_details") val teacherDetails: TeacherDetailsDto?,
    @SerializedName("parent_details") val parentDetails: ParentDetailsDto?,
    @SerializedName("school_admin_details") val schoolAdminDetails: SchoolAdminDetailsDto?,

    @SerializedName("attachments") val attachments: List<ContentAttachmentDto>?
)