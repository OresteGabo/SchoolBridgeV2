package com.schoolbridge.v2.data.dto.auth

import com.schoolbridge.v2.data.dto.common.AddressDto
import com.schoolbridge.v2.data.dto.user.student.LinkedStudentDto
import com.schoolbridge.v2.domain.user.CurrentUser
import kotlinx.serialization.Serializable
import com.schoolbridge.v2.domain.user.Gender
import kotlinx.serialization.SerialName


/**
 * Data Transfer Object (DTO) for a **user login response** received from the API.
 *
 * This DTO contains the data returned by the backend after a successful user login,
 * including authentication tokens and comprehensive user profile information.
 *
 * **Real-life Example:**
 * After a user successfully sends a [LoginRequestDto] to the `/auth/login` endpoint,
 * the backend responds with this `LoginResponseDto`. The `authToken` and detailed
 * user profile data are then saved (e.g., in `UserPreferences` via `UserSessionManager`)
 * and used for session management and displaying user-specific content.
 *
 * @property authToken The primary access token (e.g., JWT) used to authenticate subsequent API requests.
 * This token usually has a short lifespan.
 * @property refreshToken An optional, longer-lived token used to obtain new `authToken`s after
 * the current one expires, without requiring the user to re-login.
 * @property userId The unique identifier of the logged-in user.
 * @property email The email address of the logged-in user.
 * @property firstName The first name of the logged-in user.
 * @property lastName The last name of the logged-in user.
 * @property activeRoles A list of strings representing the roles the logged-in user currently possesses.
 * This helps the app quickly determine UI features and permissions.
 * @property phoneNumber The phone number of the user. Example: "+250 788 123 456"
 * @property nationalId The national identification number of the user (often masked or not directly exposed for security).
 * @property address The user's address details, encapsulated in an [CurrentUser.Address] object.
 * @property profilePictureUrl The URL to the user's profile picture.
 * @property role The primary role of the user (e.g., "Parent", "Teacher", "Student").
 * @property joinDate The date the user joined the system.
 * @property linkedStudents A list of children linked to this user, encapsulated in [CurrentUser.LinkedStudent] objects.
 * @property gender The gender of the user. (Uncomment if you decide to include and use Gender enum from your model)
 */
@Serializable
data class LoginResponseDto(
    val authToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String,
    val nationalId: String,
    val gender: String,
    val role: String,
    val activeRoles: List<String>,
    val joinDate: String,
    val address: AddressDto? = null,
    val linkedStudents: List<LinkedStudentDto> = emptyList(),

    // ✅ Fix these two
    val profilePictureUrl: String? = null,
    @SerialName("verified") val isVerified: Boolean = false
)
