package com.schoolbridge.v2.data.enums

import com.google.gson.annotations.SerializedName

/**
 * Defines the specific methods used to verify a user's identity or a specific role.
 * This provides an audit trail and context for how trust was established.
 */
enum class VerificationMethod(@SerializedName("value") val backendValue: String) {
    /**
     * Verification performed through an in-person check by a school administrator or HR.
     * Typically used for teachers, school staff, or local parents.
     * Example: Teacher presents physical National ID and academic certificates to school HR.
     */
    IN_PERSON_SCHOOL_ADMIN("IN_PERSON_SCHOOL_ADMIN"),

    /**
     * Verification based on matching names between a submitted National ID and a mobile money account (e.g., MTN MoMo).
     * The mobile money number itself would be confirmed via OTP.
     * Example: Parent provides National ID details, and their MoMo account name matches.
     */
    MOBILE_MONEY_NAME_MATCH("MOBILE_MONEY_NAME_MATCH"),

    /**
     * Verification based on the review of uploaded digital documents (e.g., scanned National ID, Passport, Birth Certificate, Family Card).
     * This typically involves manual review by an administrator.
     * Example: Diaspora parent uploads scanned passport and child's birth certificate.
     */
    UPLOADED_DOCUMENTS_REVIEW("UPLOADED_DOCUMENTS_REVIEW"),

    /**
     * Verification conducted via a live video call with an administrator, where the user presents their physical documents.
     * Useful for remote verification where high assurance is needed.
     * Example: Diaspora parent conducts a video call with a school admin to show their physical passport.
     */
    VIDEO_CALL("VIDEO_CALL"),

    /**
     * Verification through a formal attestation or letter from a recognized authority, such as a Rwandan embassy or consulate.
     * Example: Diaspora parent provides a letter from the Rwandan embassy confirming their identity and relationship.
     */
    EMBASSY_ATTESTATION("EMBASSY_ATTESTATION"),

    /**
     * The user's identity was previously verified by another trusted system or imported from a known, verified source.
     * Example: User data imported from a legacy, verified school system.
     */
    TRUSTED_SYSTEM_IMPORT("TRUSTED_SYSTEM_IMPORT"),

    /**
     * Verification based solely on email confirmation (clicking a link). This provides lower assurance and is usually for initial account activation.
     */
    EMAIL_CONFIRMATION("EMAIL_CONFIRMATION"),

    /**
     * Verification based solely on phone number confirmation (receiving an OTP). This provides lower assurance than ID-based checks.
     */
    PHONE_CONFIRMATION("PHONE_CONFIRMATION"),

    /**
     * The user's identity or a specific linkage was verified through delegation by another already verified user.
     * This method relies on the trust established with the delegating user.
     */
    DELEGATED_BY_VERIFIED_USER("DELEGATED_BY_VERIFIED_USER");

    companion object {
        fun fromBackendValue(backendValue: String): VerificationMethod {
            return entries.firstOrNull { it.backendValue == backendValue }
                ?: throw IllegalArgumentException("Unknown VerificationMethod: $backendValue")
        }
    }
}