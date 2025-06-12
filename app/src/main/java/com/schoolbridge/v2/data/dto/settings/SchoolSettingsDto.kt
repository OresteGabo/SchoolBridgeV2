package com.schoolbridge.v2.data.dto.settings

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **School Settings**.
 *
 * This DTO encapsulates various configurable settings for a specific school, as managed
 * through the backend API. These settings control how the school operates within the system,
 * affecting aspects like communication, academic periods, and feature availability.
 *
 * **When to use this class:**
 * You'll typically use `SchoolSettingsDto` when:
 * 1.  **Configuring school operations:** A school administrator updates preferences for communication,
 * enrollment, or academic year definitions.
 * 2.  **Fetching school configuration:** The application needs to know how a particular school
 * is set up (e.g., to determine default academic year, or enabled features).
 * 3.  **Displaying settings interface:** Populating a dedicated "School Settings" screen in an admin portal.
 *
 * **How to use it:**
 * This DTO would typically be fetched or updated via a dedicated API endpoint (e.g., `/api/schools/{schoolId}/settings`).
 * It represents a comprehensive set of configurations for a single school.
 *
 * @property id A unique identifier for this set of school settings. This could be the same as the school's ID if settings are directly tied one-to-one. Example: "SET_SCH001"
 * @property schoolId The ID of the school these settings apply to. Example: "SCH001"
 * @property defaultAcademicYearId The ID of the [AcademicYearDto] that is currently set as the default
 * for this school. Nullable if no default is specified. Example: "ACAYEAR_2024_2025"
 * @property defaultCurrency The default currency used for financial transactions within the school (e.g., "RWF", "USD", "EUR"). Example: "RWF"
 * @property communicationPreferences A list of strings defining preferred communication channels or
 * settings (e.g., "EMAIL_NOTIFICATIONS_ENABLED", "SMS_ALERTS_ACTIVE", "PARENT_PORTAL_MESSAGING_ENABLED"). Nullable.
 * @property enrollmentOpen A boolean indicating whether enrollment for the next academic period is currently open. Example: `true`
 * @property studentPortalEnabled A boolean indicating if the student portal features are active for this school. Example: `true`
 * @property parentPortalEnabled A boolean indicating if the parent portal features are active for this school. Example: `true`
 * @property teacherPortalEnabled A boolean indicating if the teacher portal features are active for this school. Example: `true`
 * @property autoArchiveMessageThreadsAfterDays The number of days after which message threads are automatically archived or soft-deleted. Nullable. This directly relates to your threading strategy. Example: 365
 * @property schoolTimeZone The primary time zone for the school, in IANA Time Zone Database format (e.g., "Africa/Kigali", "Europe/Paris"). Example: "Africa/Kigali"
 * @property logoUrl An optional URL to the school's primary logo. Example: "https://cdn.schoolvridge.com/logos/sch001_logo.png"
 * @property languagePreferences A list of strings defining the languages supported or preferred by the school (e.g., "en", "fr", "kin"). Nullable.
 */
data class SchoolSettingsDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("defaultAcademicYearId") val defaultAcademicYearId: String?,
    @SerializedName("defaultCurrency") val defaultCurrency: String,
    @SerializedName("communicationPreferences") val communicationPreferences: List<String>?,
    @SerializedName("enrollmentOpen") val enrollmentOpen: Boolean,
    @SerializedName("studentPortalEnabled") val studentPortalEnabled: Boolean,
    @SerializedName("parentPortalEnabled") val parentPortalEnabled: Boolean,
    @SerializedName("teacherPortalEnabled") val teacherPortalEnabled: Boolean,
    @SerializedName("autoArchiveMessageThreadsAfterDays") val autoArchiveMessageThreadsAfterDays: Int?,
    @SerializedName("schoolTimeZone") val schoolTimeZone: String,
    @SerializedName("logoUrl") val logoUrl: String?,
    @SerializedName("languagePreferences") val languagePreferences: List<String>?
)