package com.schoolbridge.v2.domain.common

/**
 * Domain model representing an in-app attachment.
 * This class defines an attachment as a reference to another entity within the application,
 * rather than an external file. It is a common concept used across various domains
 * like academic evaluations, exercises, or messaging.
 *
 * @property id Unique identifier for this specific attachment record.
 * @property attachmentType The type of entity being attached, defined by [InAppAttachmentType]
 * (e.g., [InAppAttachmentType.CHAPTER], [InAppAttachmentType.COURSE], [InAppAttachmentType.USER_PROFILE]).
 * This tells the client how to interpret [entityId] and what kind of UI to display.
 * @property entityId The unique ID of the specific entity being attached (e.g., the ID of a Chapter, a Course, or a User).
 * @property displayName A user-friendly name for the attachment, suitable for display (e.g., "Chapter 5: Photosynthesis", "Teacher John Doe's Profile").
 * @property thumbnailUrl An optional URL for a small preview image of the attached entity (e.g., a user's profile picture, a course icon).
 *
 * TODO: For future integration of external attachments (e.g., PDF, image files),
 * you could consider:
 * 1. Creating a separate, parallel domain model (e.g., `ExternalFileAttachment.kt`)
 * and using a sealed interface/class for a generic `Attachment` to combine them.
 * 2. Extending this `Attachment` model with nullable fields for external file properties
 * (e.g., `downloadUrl: String?`, `fileSize: Long?`, `mimeType: String?`), and using
 * a dedicated `AttachmentVariantType` enum to determine which fields are relevant.
 */
data class Attachment(
    val id: String,
    val attachmentType: InAppAttachmentType,
    val entityId: String,
    val displayName: String,
    val thumbnailUrl: String?
)