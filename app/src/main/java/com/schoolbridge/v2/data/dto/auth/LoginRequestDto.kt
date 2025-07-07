package com.schoolbridge.v2.data.dto.auth

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for a **user login request** sent to the API.
 *
 * This DTO encapsulates the credentials (username/email and password) that a user provides
 * to authenticate themselves and gain access to the application. It's sent to your backend's
 * login endpoint.
 *
 * **Real-life Example:**
 * When a user enters their credentials on the login screen (`OnboardingScreen.Login`) and
 * taps "Sign In", the app creates an instance of this `LoginRequestDto` and sends it to
 * the `/auth/login` endpoint.
 *
 * @property username The user's identifier for logging in. This could be their username
 * or their email address, depending on backend configuration. Example: "john.doe@example.com" or "johndoe123"
 * @property password The user's password.
 */
@Serializable
data class LoginRequestDto(
    //@SerializedName("usernameOrEmail")
    val username: String,
    //@SerializedName("password")
    val password: String
)
