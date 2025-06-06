package com.schoolbridge.v2.data.enums

import com.google.gson.annotations.SerializedName

/**
 * Defines the possible states of a user's identity verification in the system.
 * This status determines the level of trust and what actions a user is authorized to perform.
 */
enum class UserVerificationStatus(@SerializedName("value") val backendValue: String) {
    /**
     * The user's identity has not yet been verified. This is the default state after initial registration.
     */
    UNVERIFIED("UNVERIFIED"),

    /**
     * The user has submitted information or a request for identity verification, and it's awaiting review by an administrator.
     */
    PENDING_REVIEW("PENDING_REVIEW"),

    /**
     * The user's core identity (e.g., National ID, Name) has been successfully verified through one of the approved methods.
     * This status implies a high level of trust in the user's stated identity.
     */
    VERIFIED("VERIFIED"),

    /**
     * The user's identity verification request was reviewed and rejected, likely due to insufficient or fraudulent information.
     */
    REJECTED("REJECTED"),

    /**
     * The user's account is temporarily suspended by an administrator or by the user themselves.
     * While suspended, they cannot log in, but their data remains. The verification status might still be 'VERIFIED'.
     */
    SUSPENDED("SUSPENDED"),

    /**
     * The user's account has been permanently deactivated/deleted by an administrator.
     * While their data might be retained for legal/auditing purposes, the account is no longer active.
     */
    DEACTIVATED("DEACTIVATED");

    companion object {
        fun fromBackendValue(backendValue: String): UserVerificationStatus {
            return entries.firstOrNull { it.backendValue == backendValue }
                ?: throw IllegalArgumentException("Unknown UserVerificationStatus: $backendValue")
        }
    }
}