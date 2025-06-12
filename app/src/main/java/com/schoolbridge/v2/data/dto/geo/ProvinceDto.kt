package com.schoolbridge.v2.data.dto.geo

/**
 * Data Transfer Object (DTO) for a **Province**.
 *
 * This DTO represents a top-level geographical administrative division within a country.
 * It serves as the highest level in a typical nested location hierarchy (e.g., Province -> District).
 *
 * **Real-life Example:**
 * In Rwanda, "Kigali City" is a Province. When a user selects their general region or when
 * fetching a list of major regions, this DTO would be used.
 *
 * @property id A unique identifier for the province. Example: "PROV001"
 * @property name The official name of the province. Example: "Kigali City"
 */
data class ProvinceDto(
    val id: String,
    val name: String
)