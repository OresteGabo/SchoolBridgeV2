package com.schoolbridge.v2.data.preferences

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **User Preferences**.
 *
 * This DTO encapsulates various customizable settings and preferences specific to an individual user,
 * as exchanged with the backend API. These settings allow users to personalize their experience
 * within the application, affecting aspects like notification preferences, theme, and language.
 *
 * **When to use this class:**
 * You'll typically use `UserPreferencesDto` when:
 * 1.  **Fetching user preferences:** The application retrieves a user's saved preferences upon login or when
 * accessing a settings screen.
 * 2.  **Updating user preferences:** A user modifies their preferences, and the client sends these updates to the backend.
 * 3.  **Applying personalization:** The client-side application uses these preferences to adjust UI,
 * behavior, or notification delivery.
 *
 * **How to use it:**
 * This DTO would typically be fetched or updated via a dedicated API endpoint (e.g., `/api/users/{userId}/preferences`).
 * It represents a comprehensive set of personal configurations for a single user.
 *
 * @property id A unique identifier for this set of user preferences. This could be the same as the user's ID if preferences are directly tied one-to-one. Example: "PREF_USER001"
 * @property userId The ID of the user these preferences belong to. Example: "USER001"
 * @property theme A string indicating the user's preferred application theme (e.g., "LIGHT", "DARK", "SYSTEM_DEFAULT"). Example: "DARK"
 * @property preferredLanguage The user's preferred display language, typically an ISO 639-1 code (e.g., "en", "fr", "kin"). Example: "en"
 * @property timezone The user's preferred time zone, in IANA Time Zone Database format (e.g., "Africa/Kigali", "Europe/Paris"). Example: "Europe/Paris"
 * @property emailNotificationsEnabled A boolean indicating if email notifications are enabled for the user. Example: `true`
 * @property smsNotificationsEnabled A boolean indicating if SMS notifications are enabled for the user. Example: `false`
 * @property pushNotificationsEnabled A boolean indicating if mobile push notifications are enabled for the user. Example: `true`
 * @property marketingCommunicationsEnabled A boolean indicating if the user wishes to receive marketing-related communications. Example: `false`
 * @property defaultAcademicYearId The ID of the [AcademicYearDto] the user prefers to see by default (e.g., for dashboards). Nullable. Example: "ACAYEAR_2025_2026"
 * @property defaultSchoolId The ID of the [SchoolDto] the user primarily interacts with or prefers to see by default. Nullable. Example: "SCH001"
 * @property lastAccessedFeature A string indicating the last major feature or module the user accessed, for quick navigation. Nullable. Example: "MESSAGES"
 */
data class UserPreferencesDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("theme") val theme: String,
    @SerializedName("preferred_language") val preferredLanguage: String,
    @SerializedName("timezone") val timezone: String,
    @SerializedName("email_notifications_enabled") val emailNotificationsEnabled: Boolean,
    @SerializedName("sms_notifications_enabled") val smsNotificationsEnabled: Boolean,
    @SerializedName("push_notifications_enabled") val pushNotificationsEnabled: Boolean,
    @SerializedName("marketing_communications_enabled") val marketingCommunicationsEnabled: Boolean,
    @SerializedName("default_academic_year_id") val defaultAcademicYearId: String?,
    @SerializedName("default_school_id") val defaultSchoolId: String?,
    @SerializedName("last_accessed_feature") val lastAccessedFeature: String?
)