package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an **Evaluation record**.
 *
 * This DTO represents an academic evaluation, such as a quiz, exam, or project, as it is
 * exchanged with the backend API. It encapsulates details about the evaluation itself
 * and a list of the exercises that compose it.
 *
 * **When to use this class:**
 * You'll use `EvaluationDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides details of an evaluation, including
 * all exercises contained within it (e.g., for a student to view an entire exam).
 * 2.  **Sending data to the API:** Your app sends a new evaluation record (e.g., a teacher
 * creating a new exam composed of multiple exercises) to the backend.
 *
 * **How to use it:**
 * `EvaluationDto` objects are typically received from your API layer and then **mapped** to your domain
 * `Evaluation` model for comprehensive use in your application's business logic and UI.
 * It allows for displaying a structured assessment composed of individual exercises.
 *
 * @property id A unique identifier for the evaluation. Example: "EVAL_MATH_FINAL_001"
 * @property title The title of the evaluation. Example: "Mathematics Final Exam"
 * @property description A detailed description or instructions for the evaluation. Nullable.
 * @property subjectId The ID of the [SubjectDto] this evaluation belongs to. Example: "SUBJ_MATH"
 * @property typeId The ID of the [EvaluationTypeDto] (e.g., "EXAM", "QUIZ"). Example: "EVT_EXAM"
 * @property formatId The ID of the [EvaluationFormatDto] (e.g., "WRITTEN", "ORAL"). Example: "EVF_WRITTEN"
 * @property scheduledDate The date and time when the evaluation is scheduled, as an ISO 8601 datetime string. Nullable. Example: "2025-06-20T09:00:00Z"
 * @property durationMinutes The duration of the evaluation in minutes. Nullable. Example: 120
 * @property maxTotalScore The maximum possible score for the entire evaluation. Example: 200.0
 * @property exercises A list of IDs of the [ExerciseDto]s that constitute this evaluation. This creates a clear link without embedding full exercise details.
 * @property publishedDate The date the evaluation was made available, as an ISO 8601 datetime string. Example: "2025-06-10T10:00:00Z"
 * @property status The current status of the evaluation (e.g., "DRAFT", "PUBLISHED", "COMPLETED"). Example: "PUBLISHED"
 */
data class EvaluationDto(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("subjectId") val subjectId: String,
    @SerializedName("typeId") val typeId: String,
    @SerializedName("formatId") val formatId: String,
    @SerializedName("scheduledDate") val scheduledDate: String?,
    @SerializedName("durationMinutes") val durationMinutes: Int?,
    @SerializedName("maxTotalScore") val maxTotalScore: Double,
    @SerializedName("exerciseIds") val exerciseIds: List<String>, // List of exercise IDs
    @SerializedName("publishedDate") val publishedDate: String,
    @SerializedName("status") val status: String
)