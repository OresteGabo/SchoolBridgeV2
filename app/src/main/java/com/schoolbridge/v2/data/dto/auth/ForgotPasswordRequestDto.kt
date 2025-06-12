package com.schoolbridge.v2.data.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **requesting a password reset**.
 *
 * This DTO is used to initiate the "forgot password" flow. The user provides an identifier
 * (email or phone number) associated with their account, and the backend typically sends
 * a password reset link or an OTP to that identifier.
 *
 * **Real-life Example:**
 * On a "Forgot Password" screen, a user enters their email address. This `ForgotPasswordRequestDto`
 * is then sent to an endpoint like `/auth/forgot-password` to trigger the reset process.
 *
 * @property emailOrPhone The email address or phone number associated with the user's account,
 * which the backend will use to send the reset verification. Example: "user@example.com" or "+250788123456"
 */
data class ForgotPasswordRequestDto(
    @SerializedName("emailOrPhone") val emailOrPhone: String
)