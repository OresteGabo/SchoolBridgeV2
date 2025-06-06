package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Combination record**.
 *
 * This DTO represents a specific combination of academic subjects or courses offered together
 * as part of a program or curriculum, as it is exchanged with the backend API. It's often used
 * in higher education or specialized tracks.
 *
 * **When to use this class:**
 * You'll use `CombinationDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides available subject combinations (e.g., for student registration in specialized streams).
 * 2.  **Program definition:** When displaying or managing specific academic programs that consist of a predefined set of subjects.
 *
 * **How to use it:**
 * `CombinationDto` objects are typically received from your API layer and then **mapped** to your domain `Combination` model for use in your application's business logic and UI (e.g., to list subjects included in a combination or manage enrollment into a specific track).
 *
 * **Real-life Example:**
 * -   A student registering for Senior Secondary School might choose the "PCM" (Physics, Chemistry, Mathematics) combination.
 * -   The school system displays the subjects included in the "Arts & Humanities" combination.
 *
 * @property id A unique identifier for the combination. Example: "COMB_PCM"
 * @property name The name of the combination. Example: "Physics, Chemistry, Mathematics"
 * @property description A brief description of the combination or its focus. Example: "Pre-engineering science combination."
 * @property subjectIds A list of IDs of the [SubjectDto]s that constitute this combination. Example: ["SUBJ_PHY", "SUBJ_CHEM", "SUBJ_MATH"]
 */
data class CombinationDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("subject_ids") val subjectIds: List<String>
)