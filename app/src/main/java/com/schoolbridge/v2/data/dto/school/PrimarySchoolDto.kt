package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Primary School Offering**.
 *
 * This DTO represents the details and configuration for a **Primary School level**
 * offered by a school. It outlines specific aspects relevant to primary education,
 * such as grade ranges and core subjects.
 *
 * **When to use this class:**
 * You'll typically use `PrimarySchoolDto` when:
 * 1.  **Configuring a school's offerings:** An administrator sets up or modifies the Primary section.
 * 2.  **Displaying school details:** Showcasing a school's primary education programs to prospective parents.
 *
 * **How to use it:**
 * This DTO would likely be nested within a larger `SchoolDto` or used when querying specific
 * details about a school's primary provision.
 *
 * @property id A unique identifier for this specific primary school offering. Example: "PRIMARY_OFFERING_XYZ"
 * @property schoolId The ID of the parent school that offers this primary level. Example: "SCH001"
 * @property schoolLevelId The ID referencing the generic [SchoolLevelDto] for "PRIMARY". Example: "SL_PRIMARY"
 * @property minGrade The lowest grade level offered in this primary section (e.g., 1 for Grade 1). Example: 1
 * @property maxGrade The highest grade level offered in this primary section (e.g., 6 for Grade 6). Example: 6
 * @property coreSubjects A list of strings detailing the main subjects taught at this level.
 * Example: `["Mathematics", "Literacy", "Science", "Social Studies"]`
 * @property averageClassSize The average number of students per class at this level. Nullable. Example: 25
 * @property capacity The maximum number of students this primary level can accommodate. Nullable.
 */
data class PrimarySchoolDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("schoolLevelId") val schoolLevelId: String, // Should reference SchoolLevelDto.id for PRIMARY
    @SerializedName("minGrade") val minGrade: Int,
    @SerializedName("maxGrade") val maxGrade: Int,
    @SerializedName("coreSubjects") val coreSubjects: List<String>,
    @SerializedName("averageClassSize") val averageClassSize: Int?,
    @SerializedName("capacity") val capacity: Int?
)