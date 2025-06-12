package com.schoolbridge.v2.data.dto.messaging

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.messaging.attachments.InAppAttachmentDto

// import com.schoolvridge.v2.data.dto.common.InAppAttachmentDto // Ensure this path is correct

/**
 * Data Transfer Object (DTO) for a **General School Message or Announcement**.
 *
 * This DTO represents a non-threaded, official communication or announcement from the school
 * intended for a specific or broad audience. It's used for general notices, policy updates,
 * or newsletters, distinct from urgent alerts or private message threads.
 *
 * **When to use this class:**
 * You'll use `SchoolMessageDto` primarily when:
 * 1.  **Broadcasting general information:** An administrator publishes a new school policy or newsletter.
 * 2.  **Displaying notices:** Populating a general announcements section or news feed.
 * 3.  **Archiving past communications:** Reviewing historical school messages.
 *
 * **How to use it:**
 * `SchoolMessageDto` objects are received from your API layer and displayed in informational feeds.
 * They can include `InAppAttachmentDto`s to link to relevant documents or events.
 *
 * **Real-life Example:**
 * -   A message announcing the new school uniform policy.
 * -   A weekly newsletter from the principal.
 * -   A notice about upcoming extracurricular activities.
 *
 * @property id A unique identifier for this school message. Example: "SCHMSG_POLICY_001"
 * @property schoolId The ID of the school issuing this message. Example: "SCH001"
 * @property authorUserId The ID of the user (e.g., school admin, principal) who authored this message. Example: "USER_ADMIN001"
 * @property subject The main subject or title of the message. Example: "New School Uniform Policy"
 * @property content The detailed body of the message. Example: "Dear Parents and Students, please note the updated uniform guidelines..."
 * @property category An optional string indicating the type or category of this message.
 * Examples: "GENERAL_NOTICE", "POLICY_UPDATE", "NEWSLETTER", "NEWS"
 * @property targetAudience A string defining the target group for this message.
 * Examples: "ALL_STUDENTS", "ALL_PARENTS", "ALL_USERS"
 * @property publishedDate The date and time when the message was published, as an ISO 8601 datetime string. Example: "2025-06-06T10:00:00Z"
 * @property expiryDate The date and time when this message should automatically expire or be removed from active display,
 * as an ISO 8601 datetime string. Nullable. Example: "2026-06-06T00:00:00Z"
 * @property inAppAttachments A list of [InAppAttachmentDto] objects, representing previews/references
 * to other entities relevant to this message (e.g., a link to a detailed policy document, an event registration page). Nullable.
 */
data class SchoolMessageDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("authorUserId") val authorUserId: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("content") val content: String,
    @SerializedName("category") val category: String?,
    @SerializedName("targetAudience") val targetAudience: String,
    @SerializedName("publishedDate") val publishedDate: String,
    @SerializedName("expiryDate") val expiryDate: String?,
    @SerializedName("inAppAttachments") val inAppAttachments: List<InAppAttachmentDto>?
)