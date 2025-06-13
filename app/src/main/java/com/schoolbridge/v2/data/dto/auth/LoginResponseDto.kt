package com.schoolbridge.v2.data.dto.auth

import kotlinx.serialization.Serializable



import com.schoolbridge.v2.data.session.CurrentUser // Import the CurrentUser DTO for nested data classes


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
    val activeRoles: List<String>,

    val phoneNumber: String?,
    val nationalId: String?,
    val address: CurrentUser.Address?, // <--- MAKE THIS NULLABLE
    val profilePictureUrl: String?,
    val role: String?,
    val joinDate: String?,
    val linkedStudents: List<CurrentUser.LinkedStudent>? // <--- MAKE THIS NULLABLE
)
/*
// Example using a WebSocket client in Kotlin
val client = OkHttpClient()
val request = Request.Builder().url("wss://your-websocket-url").build()
val webSocket = client.newWebSocket(request, object : WebSocketListener() {
    override fun onOpen(webSocket: WebSocket, response: Response) {
        // Connection established
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        // Handle incoming message
        val update = parseUpdate(text)
        updateUI(update)
    }
})*/
