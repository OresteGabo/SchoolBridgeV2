package com.schoolbridge.v2.data.dto.geo

import kotlinx.serialization.Serializable

/**
 * Data Transfer Object (DTO) for a **District**.
 *
 * This DTO represents a geographical administrative division that typically exists within a Province.
 * It sits in the middle of a hierarchical location structure (Province -> District -> Sector).
 *
 * **Real-life Example:**
 * In Rwanda, "Kicukiro" is a District within the "Kigali City" Province. When fetching
 * districts for a selected province, or displaying a more granular location, this DTO would be used.
 *
 * @property id A unique identifier for the district. Example: "DST005"
 * @property name The official name of the district. Example: "Kicukiro"
 * @property provinceId The ID of the [ProvinceDto] this district belongs to. This creates the
 * hierarchical link. Example: "PROV001"
 */
@Serializable
data class DistrictDto(
    val id: Long,
    val name: String,
    val sectorCount: Int
)