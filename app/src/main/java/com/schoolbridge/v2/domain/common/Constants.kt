package com.schoolbridge.v2.domain.common

/**
 * An object holding common constant values used across the domain layer of the application.
 * These are typically unchanging values that are important for business logic,
 * universal limits, or core application settings, independent of UI or data storage.
 */
object Constants {
    // --- Pagination and Data Limits ---

    /**
     * Default number of items to load per page for paginated lists from the API.
     * Example: When fetching a list of courses, load 20 by default.
     */
    const val DEFAULT_PAGE_SIZE = 20

    /**
     * Maximum number of items allowed in a single list or collection for performance/display reasons.
     * Example: Max number of linked children a parent can have displayed at once without "view all".
     */
    const val MAX_ITEMS_PER_LIST_DISPLAY = 50

    /**
     * Maximum file size allowed for uploads in Megabytes (MB) before client-side validation.
     * Example: When uploading an assignment attachment, ensure it's not larger than 10MB.
     */
    const val MAX_UPLOAD_SIZE_MB = 10

    // --- User & Authentication ---

    /**
     * Minimum length required for a user's password.
     * Example: Enforced during registration and password reset validation.
     */
    const val MIN_PASSWORD_LENGTH = 8

    /**
     * Maximum length allowed for a user's password.
     * Example: Prevents extremely long passwords that might cause backend issues.
     */
    const val MAX_PASSWORD_LENGTH = 64

    /**
     * Length required for One-Time Password (OTP) verification codes.
     * Example: OTP sent to phone number must be exactly 6 digits.
     */
    const val OTP_CODE_LENGTH = 6

    /**
     * The default ID used when an item is created locally and has not yet been assigned
     * a persistent ID from the backend (e.g., for new, unsaved entities before an API call).
     */
    const val UNASSIGNED_ID = "unassigned"

    // --- Time & Durations ---

    /**
     * Default debounce delay in milliseconds for user input (e.g., search fields).
     * Example: Wait 300ms after user stops typing before performing a search query.
     */
    const val DEFAULT_DEBOUNCE_DELAY_MS = 300L

    /**
     * Duration in minutes after which an inactive user session might be considered expired on the client.
     * Example: If the user hasn't interacted with the app for 30 minutes, prompt for re-authentication.
     */
    const val SESSION_TIMEOUT_MINUTES = 30

    /**
     * Cache duration in days for certain immutable data (e.g., list of countries, provinces).
     * Example: Don't fetch the list of academic years more than once every 7 days.
     */
    const val DEFAULT_CACHE_DURATION_DAYS = 7L

    // --- Defaults & Fallbacks ---

    /**
     * Default language code for the application if not explicitly set by the user or system.
     * Example: If app language is not configured, default to English.
     */
    const val DEFAULT_LANGUAGE_CODE = "en"

    /**
     * The primary currency code used in the application for financial transactions (if applicable).
     * Example: "USD" or "RWF" for Rwandan Francs.
     */
    const val PRIMARY_CURRENCY_CODE = "RWF" // Assuming Rwandan Francs as an example

    /**
     * Generic error message to display when a more specific message is not available from the backend.
     * Example: "An unexpected error occurred. Please try again."
     */
    const val GENERIC_ERROR_MESSAGE = "An unexpected error occurred. Please try again."

    // --- Other ---

    /**
     * Regex pattern for a common email validation.
     * Example: Used for local validation of email input fields.
     */
    const val EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$"

    // TODO: Add more specific constants as your app features grow, such as:
    //  - Specific API versions if your data layer uses versioned endpoints.
    //  - Thresholds for notifications (e.g., LOW_BATTERY_WARNING_PERCENTAGE).
    //  - Feature flag identifiers (if implementing simple client-side toggles).
}