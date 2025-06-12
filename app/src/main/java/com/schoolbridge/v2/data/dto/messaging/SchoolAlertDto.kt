package com.schoolbridge.v2.data.dto.messaging

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.messaging.attachments.InAppAttachmentDto

// import com.schoolvridge.v2.data.dto.common.InAppAttachmentDto // Ensure this path is correct

/**
 * Data Transfer Object (DTO) for a **School-Wide Alert**.
 *
 * This DTO represents an important, often urgent, broadcast message or notification sent from the school
 * to a broad audience (e.g., all students, all parents). It's typically used for immediate announcements,
 * emergencies, or critical reminders.
 *
 * **When to use this class:**
 * You'll use `SchoolAlertDto` primarily when:
 * 1.  **Broadcasting urgent information:** An administrator sends an alert about a school closure.
 * 2.  **Displaying school-wide notifications:** Populating a school-wide announcement board or notification feed.
 * 3.  **Archiving past alerts:** Reviewing historical school alerts.
 *
 * **How to use it:**
 * `SchoolAlertDto` objects are received from your API layer and displayed prominently in the application.
 * They can include `InAppAttachmentDto`s to link to related entities like policies or events.
 *
 * **Real-life Example:**
 * -   An alert about an unexpected school closure due to weather.
 * -   A reminder about a critical upcoming registration deadline.
 * -   An announcement of an emergency drill.
 *
 * @property id A unique identifier for this school alert. Example: "SCHALERT_CLOSURE_001"
 * @property schoolId The ID of the school issuing this alert. Example: "SCH001"
 * @property createdByUserId The ID of the user (e.g., school admin) who created this alert. Example: "USER_ADMIN001"
 * @property targetAudience A string defining the target group for this alert.
 * Examples: "ALL_STUDENTS", "ALL_PARENTS", "ALL_STAFF", "GRADE_10_PARENTS"
 * @property type A string indicating the category or urgency of the alert.
 * Examples: "EMERGENCY", "REMINDER", "ANNOUNCEMENT", "IMPORTANT_UPDATE"
 * @property title The main heading or subject of the alert. Example: "School Closure Tomorrow!"
 * @property content The detailed message or body of the alert. Example: "Due to severe weather, SchoolVRidge Academy will be closed on Friday, June 7th."
 * @property publishedDate The date and time when the alert was published, as an ISO 8601 datetime string. Example: "2025-06-06T18:00:00Z"
 * @property expiryDate The date and time when this alert should automatically expire or be removed from active display,
 * as an ISO 8601 datetime string. Nullable. Example: "2025-06-08T00:00:00Z"
 * @property inAppAttachments A list of [InAppAttachmentDto] objects, representing previews/references
 * to other entities relevant to this alert (e.g., a link to the updated school policy). Nullable.
 */
data class SchoolAlertDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("createdByUserId") val createdByUserId: String,
    @SerializedName("targetAudience") val targetAudience: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("publishedDate") val publishedDate: String,
    @SerializedName("expiryDate") val expiryDate: String?,
    @SerializedName("inAppAttachments") val inAppAttachments: List<InAppAttachmentDto>?
)