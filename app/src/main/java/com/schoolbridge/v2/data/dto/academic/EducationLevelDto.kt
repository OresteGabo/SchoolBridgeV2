package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an **Education Level record**.
 *
 * This DTO represents a general stage or level of education (e.g., "Primary," "Secondary," "University")
 * as it is exchanged with the backend API. It helps categorize schools, students, and academic offerings.
 *
 * **When to use this class:**
 * You'll use `EducationLevelDto` primarily when:
 * 1.  **Fetching data from the API:** The backend provides a list of defined education levels (e.g., for filtering schools or defining student profiles).
 * 2.  **Categorizing entities:** When an entity (like a school or a student) needs to be associated with a specific educational stage.
 *
 * **How to use it:**
 * `EducationLevelDto` objects are typically received from your API layer and then **mapped** to your domain `EducationLevel` model (often an enum or sealed class) for type-safe usage and logic in your application.
 *
 * **Real-life Example:**
 * -   A user is registering and selects their `EducationLevelDto` as "SECONDARY" from a dropdown.
 * -   The application filters schools to show only those offering "PRIMARY" education.
 *
 * @property id A unique identifier for the education level. Example: "EDLVL_PRIMARY"
 * @property name The display name of the education level. Example: "Primary School"
 * @property description A brief description of this education level, if available. Example: "Covers grades 1-6 focusing on fundamental skills."
 */
data class EducationLevelDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?
)