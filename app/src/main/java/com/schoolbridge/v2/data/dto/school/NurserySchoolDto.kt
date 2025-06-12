package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Nursery School Offering**.
 *
 * This DTO represents the details and configuration for a **Nursery School level**
 * offered by a school. It outlines specific aspects relevant to early childhood education,
 * such as age ranges and specific facilities.
 *
 * **When to use this class:**
 * You'll typically use `NurserySchoolDto` when:
 * 1.  **Configuring a school's offerings:** An administrator sets up or modifies the Nursery section.
 * 2.  **Displaying school details:** Showcasing a school's early childhood programs to prospective parents.
 *
 * **How to use it:**
 * This DTO would likely be nested within a larger `SchoolDto` or used when querying specific
 * details about a school's nursery provision.
 *
 * @property id A unique identifier for this specific nursery school offering. Example: "NURSERY_OFFERING_ABC"
 * @property schoolId The ID of the parent school that offers this nursery level. Example: "SCH001"
 * @property schoolLevelId The ID referencing the generic [SchoolLevelDto] for "NURSERY". Example: "SL_NURSERY"
 * @property minAgeYears The minimum age (in full years) for admission to this nursery level. Example: 2
 * @property maxAgeYears The maximum age (in full years) for students in this nursery level. Example: 4
 * @property curriculumOverview A brief description of the nursery curriculum or pedagogical approach. Nullable.
 * Example: "Focus on play-based learning and early literacy."
 * @property specialFacilities A list of strings detailing unique facilities for this level (e.g., "Sensory Room", "Outdoor Play Area"). Nullable.
 * @property capacity The maximum number of students this nursery level can accommodate. Nullable.
 */
data class NurserySchoolDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("schoolLevelId") val schoolLevelId: String, // Should reference SchoolLevelDto.id for NURSERY
    @SerializedName("minAgeYears") val minAgeYears: Int,
    @SerializedName("maxAgeYears") val maxAgeYears: Int,
    @SerializedName("curriculumOverview") val curriculumOverview: String?,
    @SerializedName("specialFacilities") val specialFacilities: List<String>?,
    @SerializedName("capacity") val capacity: Int?
)