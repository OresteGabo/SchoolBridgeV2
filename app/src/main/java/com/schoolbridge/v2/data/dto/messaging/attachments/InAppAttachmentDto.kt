package com.schoolbridge.v2.data.dto.messaging.attachments

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an **In-App Message Attachment**.
 *
 * This DTO represents a **preview or reference to an existing entity** (e.g., a Chapter, an Exercise, a User)
 * that is embedded within a message thread. It is designed to provide minimal information for a quick
 * display (a "preview card") and to enable deep-linking to the full details of the referenced entity.
 *
 * This DTO **does NOT contain the full content of the entity** itself, nor does it link to external files.
 * It primarily serves as a pointer to allow the receiving client to display a rich preview and
 * fetch more details on demand.
 *
 * **When to use this class:**
 * You'll use `InAppAttachmentDto` primarily when:
 * 1.  **Sending messages:** A user (e.g., a teacher) wants to share a specific chapter or exercise
 * with students directly within a chat or announcement. They select the entity, and its `id` and
 * preview details are sent as an `InAppAttachmentDto` in the message.
 * 2.  **Receiving messages:** The client receives a message containing this DTO, renders a small
 * interactive card based on `previewTitle`, `previewDescription`, etc., and on user tap, navigates
 * to the full view of the `entityId`.
 *
 * **How to use it:**
 * `InAppAttachmentDto` objects would typically be part of a `MessageDto` (e.g.,
 * `data class MessageDto(..., val attachments: List<InAppAttachmentDto>?)`).
 * The client app uses the `entityType` to determine how to display the preview and
 * which screen to navigate to when the preview is tapped.
 *
 * **Real-life Example:**
 * -   A teacher sends a message: "Please review Chapter 5: Cell Biology for next week's quiz."
 * This message would include an `InAppAttachmentDto` with `entityType: "CHAPTER"`,
 * `entityId: "BIO101_CH05"`, `previewTitle: "Chapter 5: Cell Biology"`.
 * -   A student sends a message: "I have a question about Exercise: Algebra Practice Set."
 * This message includes an `InAppAttachmentDto` with `entityType: "EXERCISE"`,
 * `entityId: "EX_ALG_PRACTICE001"`, `previewTitle: "Algebra Practice Set"`.
 *
 * @property id A unique identifier for this specific attachment instance within the message.
 * This is distinct from the `entityId`. Example: "MSGATT_xyz456"
 * @property entityId The unique identifier of the actual entity being attached/referenced
 * (e.g., the Chapter ID, Exercise ID, User ID). Example: "BIO101_CH05"
 * @property entityType A string indicating the type of the entity being referenced. This
 * is crucial for the client to know how to interpret `entityId` and
 * what kind of preview/deep-link to render.
 * Examples: "CHAPTER", "EXERCISE", "EVALUATION", "USER", "SCHOOL", "ANNOUNCEMENT"
 * @property previewTitle A short, display-friendly title for the preview card. Example: "Chapter 5: Cell Biology"
 * @property previewDescription A brief, optional description for the preview card. Nullable.
 * Example: "Covers prokaryotic and eukaryotic cell structures."
 * @property previewImageUrl An optional URL for a small thumbnail image to display in the preview card.
 * This URL would point to an *actual external image file*. Nullable.
 * Example: "https://cdn.schoolbridge.com/thumbnails/ch5_bio.jpg"
 * @property order An optional numerical value indicating the display order of this attachment
 * within the message's list of attachments. Nullable. Example: 1
 */
data class InAppAttachmentDto(
    @SerializedName("id") val id: String,
    @SerializedName("entityId") val entityId: String,
    @SerializedName("entityType") val entityType: String,
    @SerializedName("previewTitle") val previewTitle: String,
    @SerializedName("previewDescription") val previewDescription: String?,
    @SerializedName("previewImageUrl") val previewImageUrl: String?,
    @SerializedName("order") val order: Int?
)