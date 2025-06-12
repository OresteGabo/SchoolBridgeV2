package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **new user registration request** sent to the API.
 *
 * This DTO encapsulates all the initial information required from a user to create a new
 * account on the platform. It's used when a user signs up. The `confirmPassword` field
 * is often included for backend validation to ensure the user typed their password correctly.
 *
 * **Real-life Example:**
 * When a new user fills out a "Sign Up" form in the app (e.g., on `OnboardingScreen.SignUp`),
 * the data from the form is transformed into this [RegisterRequestDto] and sent to the
 * `/auth/register` endpoint.
 *
 * @property firstName The user's first name. Example: "Aline"
 * @property lastName The user's last name. Example: "Umuhoza"
 * @property email The user's desired email address for the account. Example: "aline.umuhoza@email.com"
 * @property phoneNumber The user's phone number, used for communication or verification.
 * Example: "+250789000111"
 * @property password The user's chosen password for the account. Must meet backend complexity requirements.
 * @property confirmPassword The password typed again by the user for confirmation.
 */
data class RegisterRequestDto(
    @SerializedName("firstName") val firstName: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
)