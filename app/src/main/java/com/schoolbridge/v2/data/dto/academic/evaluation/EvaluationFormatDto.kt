package com.schoolbridge.v2.data.dto.academic.evaluation

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an **Evaluation Format record**.
 *
 * This DTO defines a specific format or structure for an evaluation (e.g., "Written Exam," "Oral Presentation," "Practical Exam")
 * as it is exchanged with the backend API. It provides details on *how* an evaluation is conducted.
 *
 * **When to use this class:**
 * You'll use `EvaluationFormatDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a list of available evaluation formats (e.g., when a teacher is scheduling a new exam and needs to specify its format).
 * 2.  **Describing evaluations:** When interpreting an `EvaluationDto`, the `formatId` would link to one of these.
 *
 * **How to use it:**
 * `EvaluationFormatDto` objects are typically received from your API layer and then **mapped** to your domain `EvaluationFormat` model (likely an enum or sealed class) for type-safe usage in your application.
 *
 * **Real-life Example:**
 * -   A school administrator defines that all final exams are of format "WRITTEN_EXAM."
 * -   A student checks the format for their upcoming science project, which is listed as "LAB_REPORT."
 *
 * @property id A unique identifier for the evaluation format. Example: "EVF_WRITTEN"
 * @property name The display name of the evaluation format. Example: "Written Exam"
 * @property description A brief description of the format, if available. Example: "Traditional paper-based examination."
 */
data class EvaluationFormatDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?
)