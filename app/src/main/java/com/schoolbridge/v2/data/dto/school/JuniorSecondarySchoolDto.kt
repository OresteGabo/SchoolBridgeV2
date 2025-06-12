package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Junior Secondary School Offering**.
 *
 * This DTO represents the details and configuration for a **Junior Secondary School level**
 * offered by a school. It outlines specific aspects relevant to this stage,
 * such as grade ranges, elective subjects, and general academic focus.
 *
 * **When to use this class:**
 * You'll typically use `JuniorSecondarySchoolDto` when:
 * 1.  **Configuring a school's offerings:** An administrator sets up or modifies the Junior Secondary section.
 * 2.  **Displaying school details:** Showcasing a school's junior secondary education programs to prospective students.
 *
 * **How to use it:**
 * This DTO would likely be nested within a larger `SchoolDto` or used when querying specific
 * details about a school's junior secondary provision.
 *
 * @property id A unique identifier for this specific junior secondary school offering. Example: "JUNIOR_SEC_OFFERING_PQR"
 * @property schoolId The ID of the parent school that offers this junior secondary level. Example: "SCH001"
 * @property schoolLevelId The ID referencing the generic [SchoolLevelDto] for "JUNIOR_SECONDARY". Example: "SL_JUNIOR_SECONDARY"
 * @property minGrade The lowest grade level offered in this junior secondary section (e.g., 7 for Grade 7). Example: 7
 * @property maxGrade The highest grade level offered in this junior secondary section (e.g., 9 for Grade 9). Example: 9
 * @property compulsorySubjects A list of strings detailing the mandatory subjects at this level.
 * Example: `["Mathematics", "English", "Physics", "Chemistry", "Biology"]`
 * @property electiveSubjects A list of strings detailing optional subjects available at this level. Nullable.
 * Example: `["Art", "Music", "Computer Science"]`
 * @property examBoards A list of strings indicating the examination boards relevant to this level (e.g., "Cambridge", "National Board"). Nullable.
 * @property capacity The maximum number of students this junior secondary level can accommodate. Nullable.
 */
data class JuniorSecondarySchoolDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("schoolLevelId") val schoolLevelId: String, // Should reference SchoolLevelDto.id for JUNIOR_SECONDARY
    @SerializedName("minGrade") val minGrade: Int,
    @SerializedName("maxGrade") val maxGrade: Int,
    @SerializedName("compulsorySubjects") val compulsorySubjects: List<String>,
    @SerializedName("electiveSubjects") val electiveSubjects: List<String>?,
    @SerializedName("examBoards") val examBoards: List<String>?,
    @SerializedName("capacity") val capacity: Int?
)