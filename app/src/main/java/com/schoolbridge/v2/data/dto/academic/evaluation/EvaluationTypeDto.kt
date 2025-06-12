package com.schoolbridge.v2.data.dto.academic.evaluation

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an **Evaluation Type record**.
 *
 * This DTO represents a general category or type of academic evaluation (e.g., "Quiz," "Exam," "Project," "Participation")
 * as it is exchanged with the backend API. It helps categorize academic assessments.
 *
 * **When to use this class:**
 * You'll use `EvaluationTypeDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a list of predefined evaluation types (e.g., when a teacher is setting up a new grade item).
 * 2.  **Categorizing evaluations:** When interpreting an `EvaluationDto`, the `typeId` would link to one of these.
 *
 * **How to use it:**
 * `EvaluationTypeDto` objects are typically received from your API layer and then **mapped** to your domain `EvaluationType` model (likely an enum or sealed class) for type-safe usage in your application.
 *
 * **Real-life Example:**
 * -   When a teacher adds a new grade, they might select "MIDTERM_EXAM" from a list of evaluation types.
 * -   A student's grade report might display "Quiz Average" by grouping grades by this type.
 *
 * @property id A unique identifier for the evaluation type. Example: "EVT_QUIZ"
 * @property name The display name of the evaluation type. Example: "Quiz"
 * @property description A brief description of the evaluation type, if available. Example: "Short assessment covering recent topics."
 */
data class EvaluationTypeDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?
)