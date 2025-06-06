package com.schoolbridge.v2.data.dto.common

import com.google.gson.annotations.SerializedName



/**
 * Data Transfer Object (DTO) for a **Content Attachment record**.
 *
 * This DTO represents a piece of content (e.g., rich text, an embedded image as Base64 string,
 * a code snippet) that is **stored directly within the database as data**, rather than being a
 * link to an external file. It serves as a flexible content block that can be associated with
 * various entities like exercises, chapters, or user profiles.
 *
 * **When to use this class:**
 * You'll use `ContentAttachmentDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides structured content embedded directly
 * within a parent entity (e.g., an exercise's instructions, supplementary chapter notes).
 * 2.  **Sending data to the API:** Your app sends new content blocks to be stored with a parent
 * entity (e.g., a teacher adds a new problem description or an image to an exercise).
 *
 * **How to use it:**
 * `ContentAttachmentDto` objects are typically found as a `List` within other DTOs (e.g.,
 * `ExerciseDto`, `ChapterDto`, `UserDto`), forming part of their primary data structure.
 * The client-side application then interprets and renders this content directly based on its `type`.
 *
 * **Real-life Example:**
 * -   An exercise has an attachment of `type: "RICH_TEXT"` containing formatted instructions
 * for a problem.
 * -   A chapter has an attachment of `type: "IMAGE_BASE64"` showing a diagram for a concept.
 * -   A user's profile has an attachment of `type: "MARKDOWN"` for their biography section.
 *
 * @property id A unique identifier for this specific content attachment. Example: "CONT_EX001_INST01"
 * @property type A string indicating the specific type or format of the content. This allows
 * the client to parse and render it appropriately.
 * Examples: "TEXT", "RICH_TEXT", "MARKDOWN", "CODE_SNIPPET", "IMAGE_BASE64", "EMBEDDED_PDF_BASE64"
 * @property title An optional title or heading for this content block. Nullable. Example: "Instructions Part 1"
 * @property content The actual content data. For text types, this is the text itself. For binary
 * types (like images), it's the Base64 encoded string of the file.
 * @property order An optional numerical value indicating the display order of this attachment
 * within its parent's list of attachments. Nullable. Example: 1
 * @property description A brief description or internal note about this content block. Nullable.
 * Example: "Main problem statement for Exercise 1."
 */
data class ContentAttachmentDto( // Renamed from AttachmentDto for clarity of its purpose
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String,
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String,
    @SerializedName("order") val order: Int?,
    @SerializedName("description") val description: String?
)