package com.schoolbridge.v2.data.dto.academic.evaluation

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.common.ContentAttachmentDto
import java.time.LocalDateTime // Or String if you prefer ISO 8601 string for DTOs

/**
 * Data Transfer Object (DTO) for an **Exercise record**.
 *
 * This DTO represents an academic exercise, assignment, or quiz, including its embedded content blocks,
 * as it is exchanged with the backend API. It contains details such as the exercise's title,
 * description, associated subject, and a list of content attachments.
 *
 * **When to use this class:**
 * You'll use `ExerciseDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides details of an exercise, including its
 * instructions, diagrams, or other embedded content (e.g., for a student to view their assignments
 * or a teacher to manage a quiz).
 * 2.  **Sending data to the API:** Your app sends a new exercise record (e.g., a teacher creating an
 * assignment with rich text instructions) to the backend.
 *
 * **How to use it:**
 * `ExerciseDto` objects are typically received from your API layer and then **mapped** to your domain
 * `Exercise` model for comprehensive use in your application's business logic and UI. The `attachments`
 * list can then be parsed and rendered directly in the UI.
 *
 * **Real-life Example:**
 * -   A student opens their "Upcoming Assignments" list and sees an `ExerciseDto` for "Chapter 5 Review Quiz,"
 * which includes multiple `ContentAttachmentDto`s for problem statements and images.
 * -   A teacher uses the app to create a new "Homework Assignment," adding a rich text description
 * and a Base64-encoded image as attachments.
 *
 * @property id A unique identifier for the exercise. Example: "EX_ALG_CH5_QUIZ"
 * @property title The title of the exercise. Example: "Chapter 5 Review Quiz"
 * @property description A general description or overview of the exercise. Nullable. Example: "Complete all questions."
 * @property subjectId The ID of the [SubjectDto] this exercise belongs to. Example: "SUBJ_MATH_ALG"
 * @property formatId The ID of the [ExerciseFormatDto] defining how this exercise is structured. Example: "EXF_SHORT_ANSWER"
 * @property dueDate The date by which the exercise is due, as an ISO 8601 date string. Nullable if no strict due date. Example: "2025-06-15"
 * @property maxScore The maximum possible score for this exercise. Example: 100.0
 * @property publishedDate The date the exercise was made available, as an ISO 8601 date string. Example: "2025-06-01"
 * @property attachments A list of [ContentAttachmentDto] objects containing embedded content
 * (e.g., rich text instructions, embedded images, code snippets) directly associated with this exercise. Nullable.
 */
data class ExerciseDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("subjectId") val subjectId: String,
    @SerializedName("formatId") val formatId: String,
    @SerializedName("dueDate") val dueDate: String?, // Changed to String for DTO
    @SerializedName("maxScore") val maxScore: Double, // Changed to Double for consistency
    @SerializedName("publishedDate") val publishedDate: String,
    @SerializedName("attachments") val attachments: List<ContentAttachmentDto>? // List of the new ContentAttachmentDto
)