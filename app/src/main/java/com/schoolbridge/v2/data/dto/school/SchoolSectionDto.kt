package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **School Section**.
 *
 * This DTO represents a specific sub-division or grouping within a larger school level (e.g.,
 * "Early Years" within Primary School, "Arts Stream" within Senior Secondary). It allows for
 * more granular organization and provides details specific to that section.
 *
 * **When to use this class:**
 * You'll typically use `SchoolSectionDto` when:
 * 1.  **Structuring school hierarchy:** An administrator organizes a school into distinct academic or administrative sections.
 * 2.  **Displaying specialized programs:** Showcasing specific streams or phases of education within a broader level.
 * 3.  **Enrollment management:** Assigning students to particular sections based on their age or academic path.
 *
 * **How to use it:**
 * This DTO would likely be nested within a [SchoolLevelOfferingDto] or directly within a [SchoolDto]
 * to provide a detailed breakdown of its educational structure.
 *
 * @property id A unique identifier for this school section. Example: "PRIMARY_EARLY_YRS"
 * @property schoolId The ID of the school to which this section belongs. Example: "SCH001"
 * @property schoolLevelOfferingId The ID of the specific [SchoolLevelOfferingDto] this section is part of.
 * This links it to its parent educational level (e.g., the Primary School offering). Example: "PRIMARY_OFFERING_XYZ"
 * @property name The name of the section (e.g., "Early Years", "Grade 3-5", "Science Stream"). Example: "Early Years"
 * @property description A brief description of the section's focus or purpose. Nullable.
 * Example: "Focuses on foundational learning for younger primary students."
 * @property minGrade The lowest grade level included in this section. Nullable. Example: 1
 * @property maxGrade The highest grade level included in this section. Nullable. Example: 2
 * @property capacity The maximum number of students this section can accommodate. Nullable.
 * @property isActive A boolean indicating whether this section is currently active or open for enrollment. Example: `true`
 */
data class SchoolSectionDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("schoolLevelOfferingId") val schoolLevelOfferingId: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("minGrade") val minGrade: Int?,
    @SerializedName("maxGrade") val maxGrade: Int?,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("isActive") val isActive: Boolean
)