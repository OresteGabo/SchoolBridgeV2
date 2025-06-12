package com.schoolbridge.v2.data.dto.messaging

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.messaging.attachments.InAppAttachmentDto

// import com.schoolvridge.v2.data.dto.common.InAppAttachmentDto // Ensure this path is correct

/**
 * Data Transfer Object (DTO) for a **Message Thread**.
 *
 * This DTO represents an entire, topic-specific conversation thread, consisting of multiple individual messages.
 * As per your design, each distinct "matter" or topic should initiate a new thread, similar to email subjects.
 * Threads are designed to be self-contained and may have an auto-deletion policy to manage database size.
 *
 * **When to use this class:**
 * You'll use `MessageThreadDto` primarily when:
 * 1.  **Listing threads:** Displaying a user's inbox or list of ongoing conversations.
 * 2.  **Opening a thread:** Retrieving all messages within a specific thread to display the conversation.
 * 3.  **Creating a new thread:** Initiating a new conversation on a distinct topic.
 * 4.  **Managing thread lifecycle:** Tracking last message, participants, and auto-deletion dates.
 *
 * **How to use it:**
 * `MessageThreadDto` objects are received from your API layer. The nested `messages` list contains
 * the individual messages in chronological order. `InAppAttachmentDto`s can be embedded within
 * individual messages to link to other entities.
 *
 * **Real-life Example:**
 * -   A parent creates a `MessageThreadDto` with the subject "Question about weekend event" to discuss an event.
 * -   A teacher replies within that thread.
 * -   Later, the parent wants to ask about homework, so they create a *new* `MessageThreadDto` for that new topic.
 *
 * @property id A unique identifier for this message thread. Example: "THREAD_EVENT_WKND_001"
 * @property subject The main topic or subject of the conversation thread. Example: "Inquiry about Saturday's Sports Day"
 * @property participants A list of IDs of the users involved in this thread. Example: `["USER_PARENT001", "USER_TEACHER003"]`
 * @property createdAt The date and time when the thread was initiated, as an ISO 8601 datetime string. Example: "2025-06-01T10:00:00Z"
 * @property lastMessageAt The date and time of the most recent message in this thread, as an ISO 8601 datetime string. Example: "2025-06-05T14:15:00Z"
 * @property threadType A string indicating the category or nature of the thread.
 * Examples: "PARENT_TEACHER_CHAT", "STUDENT_SUPPORT", "GENERAL_INQUIRY", "EVENT_DISCUSSION"
 * @property relatedEntityId An optional ID of another entity (e.g., a `SchoolEventDto`, `ExerciseDto`)
 * if this thread is directly related to it. Nullable. Example: "EVENT_SPORTS_WKND"
 * @property relatedEntityType An optional string indicating the type of entity `relatedEntityId` refers to.
 * Examples: "SCHOOL_EVENT", "EXERCISE", "EVALUATION". Nullable.
 * @property isArchived A boolean indicating if the thread has been archived by the viewing user. Example: `false`
 * @property autoDeleteDate The specific date and time when this thread is scheduled for automatic deletion,
 * as an ISO 8601 datetime string. Nullable if no auto-deletion set. Example: "2026-06-01T00:00:00Z"
 * @property messages A list of [MessageInThreadDto] objects representing the individual messages
 * within this thread, typically ordered chronologically. This list might be paginated or truncated
 * for large threads.
 */
data class MessageThreadDto(
    @SerializedName("id") val id: String,
    @SerializedName("subject") val subject: String,
    @SerializedName("participants") val participants: List<String>,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("lastMessageAt") val lastMessageAt: String,
    @SerializedName("threadType") val threadType: String,
    @SerializedName("relatedEntityId") val relatedEntityId: String?,
    @SerializedName("relatedEntityType") val relatedEntityType: String?,
    @SerializedName("isArchived") val isArchived: Boolean,
    @SerializedName("autoDeleteDate") val autoDeleteDate: String?,
    @SerializedName("messages") val messages: List<MessageInThreadDto>
)

/**
 * Data Transfer Object (DTO) for an **Individual Message within a Thread**.
 *
 * This DTO represents a single message that is part of a larger [MessageThreadDto].
 * It contains the sender, content, timestamp, and any embedded entity previews.
 * This DTO is typically nested within [MessageThreadDto] lists.
 *
 * @property id A unique identifier for this individual message. Example: "MSG_001_IN_THREAD_A"
 * @property senderId The ID of the user who sent this message. Example: "USER_TEACHER003"
 * @property content The actual text content of the message. Example: "Yes, Sports Day begins at 9 AM."
 * @property sentAt The date and time when the message was sent, as an ISO 8601 datetime string. Example: "2025-06-05T14:15:00Z"
 * @property readBy A list of IDs of the users who have read this specific message. Nullable.
 * This can be used for read receipts. Example: `["USER_PARENT001"]`
 * @property inAppAttachments A list of [InAppAttachmentDto] objects, representing previews/references
 * to other entities embedded within this message (e.g., a link to a school event or an exercise). Nullable.
 */
data class MessageInThreadDto(
    @SerializedName("id") val id: String,
    @SerializedName("senderId") val senderId: String,
    @SerializedName("content") val content: String,
    @SerializedName("sentAt") val sentAt: String,
    @SerializedName("readBy") val readBy: List<String>?,
    @SerializedName("inAppAttachments") val inAppAttachments: List<InAppAttachmentDto>?
)