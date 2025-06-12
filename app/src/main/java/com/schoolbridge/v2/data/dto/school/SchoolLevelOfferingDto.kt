package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **School Level Offering**.
 *
 * This DTO provides a generic representation of any educational level (Nursery, Primary, Secondary, etc.)
 * that a specific school offers. It links to the generic [SchoolLevelDto] to identify the type of level,
 * and includes common attributes that apply across different levels.
 *
 * **When to use this class:**
 * You'll typically use `SchoolLevelOfferingDto` when:
 * 1.  **Listing a school's total offerings:** To get a summary of all levels a school provides without needing
 * to know the specific details of each.
 * 2.  **Representing a polymorphic offering:** When an API needs to return a list of various school levels,
 * this DTO can act as a base or union type.
 * 3.  **Basic reporting:** To quickly see which levels a school supports.
 *
 * **How to use it:**
 * This DTO can be used as a list within a main `SchoolDto` to describe its educational structure.
 * It might also be extended by more specific DTOs (like `NurserySchoolDto`, `PrimarySchoolDto`)
 * if distinct details for each level type are needed (though for this request, I've created them as separate,
 * parallel DTOs for distinct attributes).
 *
 * @property id A unique identifier for this specific school level offering. Example: "SCH_OFFER_NUR_001"
 * @property schoolId The ID of the school providing this level offering. Example: "SCH001"
 * @property schoolLevelId The ID referencing the generic [SchoolLevelDto] that this offering represents
 * (e.g., "SL_NURSERY", "SL_PRIMARY"). Example: "SL_PRIMARY"
 * @property isActive A boolean indicating whether this specific level offering is currently active or open for enrollment. Example: `true`
 * @property intakeCapacity The total number of students this specific level offering can accommodate annually. Nullable. Example: 300
 * @property contactPersonUserId The ID of a user (e.g., a head of department) responsible for this level. Nullable.
 * @property feeStructureId An optional ID linking to a specific fee structure related to this level. Nullable.
 */
data class SchoolLevelOfferingDto(
    @SerializedName("id") val id: String,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("schoolLevelId") val schoolLevelId: String,
    @SerializedName("isActive") val isActive: Boolean,
    @SerializedName("intakeCapacity") val intakeCapacity: Int?,
    @SerializedName("contactPersonUserId") val contactPersonUserId: String?,
    @SerializedName("feeStructureId") val feeStructureId: String?
)