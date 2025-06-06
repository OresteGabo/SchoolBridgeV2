package com.schoolbridge.v2.domain.common

/**
 * A sealed class representing the status of a validation operation.
 * This allows for different types of success/failure states, potentially carrying data.
 */
sealed class ValidationStatus {
    /**
     * Indicates that the validation was successful.
     * Example: `ValidationStatus.Success` for a valid email.
     */
    object Success : ValidationStatus()

    /**
     * Indicates that the validation failed due to an invalid input.
     * @property message A user-friendly message explaining why the validation failed.
     * Example: `ValidationStatus.Invalid("Password must be at least 8 characters long.")`
     */
    data class Invalid(val message: String) : ValidationStatus()

    /**
     * Indicates that the validation could not be completed due to a network or server issue.
     * @property errorMessage An optional message providing more details about the error.
     * Example: `ValidationStatus.Error("Failed to connect to the server.")`
     */
    data class Error(val errorMessage: String? = null) : ValidationStatus()

    /**
     * Indicates that the validation is currently in progress.
     * Example: `ValidationStatus.Loading` while checking if a username is unique.
     */
    object Loading : ValidationStatus()
}