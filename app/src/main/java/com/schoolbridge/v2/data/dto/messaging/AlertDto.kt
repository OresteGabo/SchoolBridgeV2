package com.schoolbridge.v2.data.dto.messaging

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **User-Specific Alert or Notification**.
 *
 * This DTO represents a personalized alert or notification intended for a single user,
 * as it is exchanged with the backend API. It's often used for reminders, warnings,
 * or direct actionable notifications.
 *
 * **When to use this class:**
 * You'll use `AlertDto` primarily when:
 * 1.  **Fetching user alerts:** The backend provides a list of unread or active alerts for a specific user.
 * 2.  **Marking alerts:** The client marks an alert as read or dismisses it.
 * 3.  **Displaying notifications:** To show banners, pop-ups, or a dedicated alert list to the user.
 *
 * **How to use it:**
 * `AlertDto` objects are typically received from your API layer and then **mapped** to your domain
 * `Alert` model for display and user interaction. They might link to other entities for context.
 *
 * **Real-life Example:**
 * -   A student receives an `AlertDto` reminding them of an upcoming assignment due date.
 * -   A parent receives an `AlertDto` about a payment overdue.
 * -   The client fetches a list of alerts to populate a notification center badge.
 *
 * @property id A unique identifier for this alert. Example: "ALERT_PAY_OVERDUE_001"
 * @property userId The ID of the user to whom this alert is directed. Example: "USER001"
 * @property type A string indicating the category or urgency of the alert.
 * Examples: "REMINDER", "URGENT", "WARNING", "INFO"
 * @property title The main heading or subject of the alert. Example: "Tuition Payment Overdue!"
 * @property content The detailed message or body of the alert. Example: "Your tuition for Term 2 was due on 2025-05-30."
 * @property sentDate The date and time when the alert was generated/sent, as an ISO 8601 datetime string. Example: "2025-06-05T09:00:00Z"
 * @property isRead A boolean indicating whether the user has marked this alert as read. Example: `false`
 * @property linkToEntityId An optional ID of another entity (e.g., an `ExerciseDto`, `InvoiceDto`, `MessageThreadDto`)
 * that this alert relates to, allowing for deep-linking. Nullable. Example: "INV005"
 * @property linkToEntityType An optional string indicating the type of entity `linkToEntityId` refers to.
 * Examples: "INVOICE", "EXERCISE", "MESSAGE_THREAD". Nullable.
 * @property expiryDate The date and time when this alert should automatically expire or be removed,
 * as an ISO 8601 datetime string. Nullable. Example: "2025-06-15T23:59:59Z"
 */
data class AlertDto(
    @SerializedName("id") val id: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("sent_date") val sentDate: String,
    @SerializedName("is_read") val isRead: Boolean,
    @SerializedName("link_to_entity_id") val linkToEntityId: String?,
    @SerializedName("link_to_entity_type") val linkToEntityType: String?,
    @SerializedName("expiry_date") val expiryDate: String?
)