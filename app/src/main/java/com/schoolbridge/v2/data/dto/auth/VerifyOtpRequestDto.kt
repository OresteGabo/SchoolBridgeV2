package com.schoolbridge.v2.data.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **verifying a One-Time Password (OTP)**.
 *
 * This DTO is used to submit an OTP that a user has received (e.g., via SMS or email)
 * to confirm their identity or complete a specific action. It's a common component in
 * multi-factor authentication, account verification, and password reset flows.
 *
 * **Real-life Example:**
 * - **Phone Number Verification:** After a user registers, an OTP is sent to their phone. They enter
 * it into the app, and this DTO is sent to `/auth/verify-otp`.
 * `VerifyOtpRequestDto(emailOrPhone="+250788123456", otpCode="789012", purpose=OtpPurpose.PHONE_VERIFICATION)`
 * - **Password Reset Confirmation:** After requesting a password reset, an OTP is sent to their email.
 * They then use this DTO to confirm ownership before setting a new password.
 * `VerifyOtpRequestDto(emailOrPhone="user@example.com", otpCode="345678", purpose=OtpPurpose.PASSWORD_RESET)`
 *
 * @property emailOrPhone The identifier (email address or phone number) to which the OTP was sent.
 * @property otpCode The One-Time Password code entered by the user.
 * @property purpose An optional [OtpPurpose] indicating the specific context or reason for OTP verification.
 * This helps the backend differentiate between different OTP flows.
 */
data class VerifyOtpRequestDto(
    @SerializedName("email_or_phone") val emailOrPhone: String,
    @SerializedName("otp_code") val otpCode: String,
    @SerializedName("purpose") val purpose: OtpPurpose? = null // Now uses the enum!
)