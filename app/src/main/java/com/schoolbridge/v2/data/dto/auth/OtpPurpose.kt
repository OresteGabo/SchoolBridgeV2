package com.schoolbridge.v2.data.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Defines the various purposes for which a One-Time Password (OTP) can be used.
 *
 * Each enum entry maps to a specific string value that is expected by the backend API.
 * This provides type safety and clarity when sending OTP requests.
 *
 * @property backendValue The string representation of the purpose as expected by the backend API.
 */
enum class OtpPurpose(@SerializedName("value") val backendValue: String) {
    /**
     * OTP used to verify ownership of an email address, typically during registration or email update.
     * Real-life example: User signs up, receives OTP in email, enters it to activate account.
     */
    EMAIL_VERIFICATION("EMAIL_VERIFICATION"),

    /**
     * OTP used to verify ownership of a phone number, typically during registration or phone number update.
     * Real-life example: User adds phone number to profile, receives SMS OTP, enters it to confirm.
     */
    PHONE_VERIFICATION("PHONE_VERIFICATION"),

    /**
     * OTP used as part of a password reset flow to confirm user identity before setting a new password.
     * Real-life example: User clicks 'Forgot Password', receives OTP via email/SMS, enters it to proceed.
     */
    PASSWORD_RESET("PASSWORD_RESET"),

    /**
     * OTP used for two-factor authentication (2FA) during login or for sensitive actions.
     * Real-life example: User logs in, then receives OTP to enter for extra security.
     */
    TWO_FACTOR_AUTHENTICATION("TWO_FACTOR_AUTHENTICATION"),

    /**
     * OTP used to confirm critical transactions or irreversible actions.
     * Real-life example: User initiates a payment or a significant data change, OTP is sent for final approval.
     */
    TRANSACTION_CONFIRMATION("TRANSACTION_CONFIRMATION"),

    /**
     * OTP used to confirm the user's intent to delete their account.
     * Real-life example: User requests account deletion, receives OTP to confirm this sensitive action.
     */
    ACCOUNT_DELETION("ACCOUNT_DELETION");

    // You can add a companion object or helper functions here if needed,
    // for example, to convert a string from the backend back to an enum.
    companion object {
        /**
         * Returns the [OtpPurpose] corresponding to the given [backendValue] string.
         * @throws IllegalArgumentException if no matching purpose is found.
         */
        fun fromBackendValue(backendValue: String): OtpPurpose {
            return entries.firstOrNull { it.backendValue == backendValue }
                ?: throw IllegalArgumentException("Unknown OtpPurpose: $backendValue")
        }
    }
}