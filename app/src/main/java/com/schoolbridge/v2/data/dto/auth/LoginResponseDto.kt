package com.schoolbridge.v2.data.dto.auth

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for a **user login response** received from the API.
 *
 * This DTO contains the data returned by the backend after a successful user login.
 * It typically includes authentication tokens necessary for subsequent authenticated API calls,
 * and basic user information to quickly identify the logged-in user.
 *
 * **Real-life Example:**
 * After a user successfully sends a [LoginRequestDto] to the `/auth/login` endpoint,
 * the backend responds with this `LoginResponseDto`. The `authToken` is then saved
 * (e.g., in `UserPreferences`) and used in HTTP headers for future requests.
 *
 * @property authToken The primary access token (e.g., JWT) used to authenticate subsequent API requests.
 * This token usually has a short lifespan. Example: `"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjE1MTYyMzkwODJ9.Signature"`
 * @property refreshToken An optional, longer-lived token used to obtain new `authToken`s after
 * the current one expires, without requiring the user to re-login.
 * @property userId The unique identifier of the logged-in user. Example: "usr_abc123"
 * @property email The email address of the logged-in user. Example: "user@example.com"
 * @property firstName The first name of the logged-in user. Example: "Alice"
 * @property lastName The last name of the logged-in user. Example: "Smith"
 * @property activeRoles A list of strings representing the roles the logged-in user currently possesses.
 * This helps the app quickly determine UI features and permissions. Example: `["STUDENT", "PARENT"]`, `["TEACHER"]`
 */
@Serializable
data class LoginResponseDto(
    //@SerialName("authToken")
    val authToken: String,
    //@SerialName("refreshToken")
    val refreshToken: String,
    //@SerialName("userId")
    val userId: String,
    //@SerialName("email")
    val email: String, // Assuming your backend response includes email
    //@SerialName("firstName")
    val firstName: String,
    //@SerialName("lastName")
    val lastName: String,
    //@SerialName("activeRoles")
    val activeRoles: List<String>
)