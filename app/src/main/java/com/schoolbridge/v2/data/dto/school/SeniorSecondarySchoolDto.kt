package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Senior Secondary School Offering**.
 *
 * This DTO represents the details and configuration for a **Senior Secondary School level**
 * offered by a school. It outlines specific aspects relevant to pre-university education,
 * such as advanced grade ranges, specialization streams, and university preparation.
 *
 * **When to use this class:**
 * You'll typically use `SeniorSecondarySchoolDto` when:
 * 1.  **Configuring a school's offerings:** An administrator sets up or modifies the Senior Secondary section.
 * 2.  **Displaying school details:** Showcasing a school's senior secondary programs to prospective students.
 * 3.  **Managing academic streams:** Defining and assigning students to specialized academic paths.
 *
 * **How to use it:**
 * This DTO would likely be nested within a larger [SchoolDto] or used when querying specific
 * details about a school's senior secondary provision.
 *
 * @property id A unique identifier for this specific senior secondary school offering. Example: "SENIOR_SEC_OFFERING_DEF"
 * @property schoolId The ID of the parent school that offers this senior secondary level. Example: "SCH001"
 * @property schoolLevelId The ID referencing the generic [SchoolLevelDto] for "SENIOR_SECONDARY". Example: "SL_SENIOR_SECONDARY"
 * @property minGrade The lowest grade level offered in this senior secondary section (e.g., 10 for Grade 10). Example: 10
 * @property maxGrade The highest grade level offered in this senior secondary section (e.g., 12 for Grade 12). Example: 12
 * @property academicStreams A list of strings detailing the available academic specialization streams (e.g., "Science", "Arts", "Commerce"). Nullable.
 * Example: `["Science (PCM)", "Arts (History & Lit)", "Commerce"]`
 * @property advancedSubjects A list of strings detailing advanced or optional subjects available at this level. Nullable.
 * Example: `["Calculus", "Advanced Chemistry", "World History"]`
 * @property universityPreparationPrograms A list of strings detailing any specific university preparation programs offered. Nullable.
 * Example: `["SAT Prep", "Career Counseling", "University Application Workshops"]`
 * @property examBoards A list of strings indicating the examination boards relevant to this level (e.g., "IB", "A-Levels", "National Board"). Nullable.
 * @property capacity The maximum number of students this senior secondary level can accommodate. Nullable.
 */
data class SeniorSecondarySchoolDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("schoolLevelId") val schoolLevelId: String, // Should reference SchoolLevelDto.id for SENIOR_SECONDARY
    @SerializedName("minGrade") val minGrade: Int,
    @SerializedName("maxGrade") val maxGrade: Int,
    @SerializedName("academicStreams") val academicStreams: List<String>?,
    @SerializedName("advancedSubjects") val advancedSubjects: List<String>?,
    @SerializedName("universityPreparationPrograms") val universityPreparationPrograms: List<String>?,
    @SerializedName("examBoards") val examBoards: List<String>?,
    @SerializedName("capacity") val capacity: Int?
)