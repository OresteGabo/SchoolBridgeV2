package com.schoolbridge.v2.data.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **requesting a new One-Time Password (OTP) to be sent**.
 *
 * This DTO is used when a user needs a fresh OTP, for instance, if the previous one expired,
 * was not received, or was accidentally deleted. It's often used in conjunction with
 * verification and password reset flows.
 *
 * **Real-life Example:**
 * On an OTP entry screen, a "Resend Code" button might trigger an API call using this DTO.
 * `ResendOtpRequestDto(emailOrPhone="+250788123456", purpose=OtpPurpose.PHONE_VERIFICATION)`
 *
 * @property emailOrPhone The email address or phone number to which the new OTP should be sent.
 * @property purpose An optional [OtpPurpose] specifying the context for which the OTP is needed.
 * This helps the backend know which type of OTP to generate and send.
 */
data class ResendOtpRequestDto(
    @SerializedName("emailOrPhone") val emailOrPhone: String,
    @SerializedName("purpose") val purpose: OtpPurpose? = null // Now uses the enum!
)