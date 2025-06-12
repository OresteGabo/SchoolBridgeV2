package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Generic School Level**.
 *
 * This DTO defines a standardized educational level (e.g., "NURSERY", "PRIMARY", "JUNIOR_SECONDARY").
 * It serves as a type definition rather than a specific offering by a school. Other DTOs,
 * like [SchoolLevelOfferingDto], will reference these definitions.
 *
 * **When to use this class:**
 * You'll typically use `SchoolLevelDto` when:
 * 1.  **Defining lookup data:** Populating dropdowns or lists of available educational levels in an admin panel.
 * 2.  **Referencing types:** Other DTOs link to this to specify the type of school level.
 *
 * **How to use it:**
 * This DTO is usually part of a static or configuration data set. It's referenced by its `id` or `name`
 * by other DTOs that need to specify an educational level.
 *
 * @property id A unique identifier for this school level. Example: "SL_PRIMARY"
 * @property name A display-friendly name for the school level. Example: "Primary School"
 * @property description A brief description of this school level. Nullable.
 * Example: "Education typically for ages 6-12."
 * @property order A numerical value indicating the typical progression order of school levels. Example: 2
 */
data class SchoolLevelDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("order") val order: Int
)