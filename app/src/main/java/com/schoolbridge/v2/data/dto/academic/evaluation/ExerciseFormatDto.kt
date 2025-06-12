package com.schoolbridge.v2.data.dto.academic.evaluation

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an **Exercise Format record**.
 *
 * This DTO defines a specific format or type of exercise (e.g., "Multiple Choice," "Essay," "Practical Lab")
 * as it is exchanged with the backend API. It's used to categorize and standardize exercises.
 *
 * **When to use this class:**
 * You'll use `ExerciseFormatDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a list of available exercise formats (e.g., when a teacher is creating a new exercise and needs to select its format).
 * 2.  **Identifying formats:** When interpreting an `ExerciseDto`, the `formatId` would link to one of these.
 *
 * **How to use it:**
 * `ExerciseFormatDto` objects are typically received from your API layer and then **mapped** to your domain `ExerciseFormat` model, which might be an enum or a sealed class, for type-safe usage in your application.
 *
 * **Real-life Example:**
 * -   A teacher selects "SHORT_ANSWER" from a dropdown list of available exercise formats when creating a new quiz question.
 * -   The app displays the format name "Multiple Choice" for a given exercise.
 *
 * @property id A unique identifier for the exercise format. Example: "EXF_MCQ"
 * @property name The display name of the exercise format. Example: "Multiple Choice Question"
 * @property description A brief description of the format, if available. Example: "Questions with a single correct answer from a list of options."
 */
data class ExerciseFormatDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?
)