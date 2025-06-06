package com.schoolbridge.v2.data.dto.academic.evaluation

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Subject record**.
 *
 * This DTO represents an academic subject or course of study as it is exchanged with the backend API.
 * It encapsulates basic information about the subject, such as its unique identifier and name.
 *
 * **When to use this class:**
 * You'll use `SubjectDto` primarily when:
 * 1.  **Fetching data from the API:** The backend sends a list of available subjects (e.g., for course selection or a curriculum overview), which your app deserializes into `SubjectDto` objects.
 * 2.  **Sending data to the API:** Your app might need to identify a subject by its ID when creating or updating other academic records.
 *
 * **How to use it:**
 * Typically, `SubjectDto` objects are received from your API layer and then **mapped** to your domain `Subject` model for use in your application's business logic and UI.
 *
 * **Real-life Example:**
 * -   When a student views their curriculum, the app fetches `SubjectDto`s for "Mathematics," "Physics," and "History."
 * -   When assigning a new course, the app sends the `id` of a `SubjectDto` to the backend.
 *
 * @property id A unique identifier for the subject. Example: "SUBJ_MATH_ALG"
 * @property name The name of the subject. Example: "Algebra"
 * @property code An optional short code or abbreviation for the subject. Example: "ALG101"
 */
data class SubjectDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("code") val code: String?
)