package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Faculty** within a University or a large educational institution.
 *
 * This class represents a major academic division, such as the "Faculty of Engineering," "Faculty of Arts,"
 * or "Faculty of Sciences." Faculties often group together several related departments.
 *
 * **When to use this class:**
 * You'll use `FacultyDto` when your application needs to display or manage information about these
 * high-level academic units. This is particularly relevant for university-level systems.
 *
 * **Example Situations:**
 * - **Displaying University Structure:** A university portal showing a list of all its faculties.
 * - **Assigning Dean Roles:** The system needs to link a specific user (the Dean) to a faculty.
 * - **Reporting:** Generating reports aggregated by faculty (e.g., student enrollment per faculty).
 *
 * @property id A unique identifier for the faculty. Example: "FAC001"
 * @property name The full name of the faculty. Example: "Faculty of Engineering and Technology"
 * @property description An optional, brief description of the faculty's focus or academic areas.
 * @property deanUserId The unique ID of the user who is currently the Dean of this faculty.
 * This links the faculty to a specific user account (e.g., a teacher or administrator with a special 'Dean' role).
 * Nullable if the Dean position is vacant or not tracked in the system. Example: "usr_dean_eng"
 */
data class FacultyDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("deanUserId") val deanUserId: String? // ID of the user who is the dean
)