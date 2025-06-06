package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.common.ContentAttachmentDto


/**
 * Data Transfer Object (DTO) for a **Chapter record**.
 *
 * This DTO represents a chapter or unit within a larger academic subject or course, as it is exchanged
 * with the backend API. It encapsulates details like the chapter's title, order, associated subject,
 * and now, directly embedded content blocks.
 *
 * **When to use this class:**
 * You'll use `ChapterDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a list of chapters for a specific course,
 * including their content (e.g., for a student to navigate course content).
 * 2.  **Structuring content:** When displaying a syllabus or progress tracking for a subject,
 * where chapter details and their embedded content are retrieved.
 *
 * **How to use it:**
 * `ChapterDto` objects are typically received from your API layer and then **mapped** to your domain `Chapter` model.
 * The `attachments` list containing `ContentAttachmentDto`s can then be parsed and rendered directly
 * in the UI to display the chapter's actual content.
 *
 * **Real-life Example:**
 * -   A student opens their "Biology I" course and sees a list of `ChapterDto`s. Clicking on one
 * reveals its title, description, and a list of `attachments` which might include rich text notes
 * and a diagram for "The Cell."
 * -   A teacher organizes their course content by adding new chapters via the application,
 * directly embedding lecture slides or problem sets as `ContentAttachmentDto`s.
 *
 * @property id A unique identifier for the chapter. Example: "BIO101_CH01"
 * @property title The title of the chapter. Example: "The Cell: Structure and Function"
 * @property subjectId The ID of the [SubjectDto] this chapter belongs to. Example: "SUBJ_BIO101"
 * @property order A numerical value indicating the sequential order of the chapter within its subject. Example: 1
 * @property description A brief summary or learning objectives for the chapter, if available. Example: "Covers prokaryotic and eukaryotic cell structures."
 * @property attachments A list of [ContentAttachmentDto] objects containing embedded content
 * directly associated with this chapter (e.g., lecture notes, diagrams, study guides). Nullable.
 */
data class ChapterDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("subject_id") val subjectId: String,
    @SerializedName("order") val order: Int,
    @SerializedName("description") val description: String?,
    @SerializedName("attachments") val attachments: List<ContentAttachmentDto>?
)