package com.schoolbridge.v2.data.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **completing a password reset**.
 *
 * This DTO is used after a user has initiated a password reset (e.g., via [ForgotPasswordRequestDto])
 * and verified their identity (e.g., via [VerifyOtpRequestDto] or a reset link). It contains the
 * necessary token for verification and the user's chosen new password.
 *
 * **Real-life Example:**
 * After a user clicks a password reset link in an email (which might open the app with a token)
 * or enters an OTP, they are prompted to set a new password. The values from this form are sent
 * to the backend (e.g., `/auth/reset-password`) using this DTO.
 *
 * @property token The verification token received by the user (e.g., from an email link, or the OTP itself).
 * This token validates their right to reset the password. Example: `"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJwYXNzd29yZHJlc2V0IiwiaWQiOiJ1c3JfYWJjMTIzIiwiaWF0IjoxNzA0MDY3MjAwLCJleHAiOjE3MDQwNjcwNjB9.SomeRandomHash"`
 * @property newPassword The new password chosen by the user.
 * @property confirmNewPassword The new password repeated for backend validation, ensuring consistency.
 */
data class ResetPasswordRequestDto(
    @SerializedName("token") val token: String,
    @SerializedName("newPassword") val newPassword: String,
    @SerializedName("confirmNewPassword") val confirmNewPassword: String
)