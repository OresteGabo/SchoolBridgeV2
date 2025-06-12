package com.schoolbridge.v2.data.dto.geo

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Sector**.
 *
 * This DTO represents a geographical administrative division that typically exists within a District.
 * It's part of a hierarchical location structure (Province -> District -> Sector -> Cell -> Village).
 *
 * **Real-life Example:**
 * In Rwanda, "Gikondo" is a Sector within the "Kicukiro" District. When displaying an address or
 * allowing users to select their location, this DTO would represent such a geographical unit.
 *
 * @property id A unique identifier for the sector. Example: "SCT001"
 * @property name The official name of the sector. Example: "Gikondo"
 * @property districtId The ID of the [DistrictDto] this sector belongs to. This creates the
 * hierarchical link. Example: "DST005"
 */
data class SectorDto(
    val id: String,
    val name: String,
    @SerializedName("districtId") val districtId: String
)