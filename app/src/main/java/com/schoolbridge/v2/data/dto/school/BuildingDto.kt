package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Building** within a School or Campus.
 *
 * This class represents a specific physical structure (e.g., "Science Block," "Admin Building," "Library")
 * that houses classrooms, offices, labs, or other school facilities.
 *
 * **When to use this class:**
 * You'll use `BuildingDto` to organize and manage facilities within a school or a specific campus,
 * especially when dealing with larger institutions that have multiple distinct structures.
 *
 * **Example Situations:**
 * - **Indoor Navigation:** Helping users (students, visitors) find their way to specific buildings on a campus.
 * - **Facility Management:** Tracking maintenance schedules or resource allocation per building.
 * - **Emergency Services:** Providing clear locations for emergency response.
 * - **Asset Tracking:** Managing inventory of assets (e.g., computers, furniture) by building.
 *
 * @property id A unique identifier for the building. Example: "BUILD_SCI"
 * @property name The common name or designation of the building (e.g., "Science Block", "Administration Building", "Library").
 * @property campusId An optional unique ID of the campus this building is part of. This is used when
 * the school has multiple campuses. Nullable if the building belongs directly to a single-campus school.
 * Example: "CAMPUS_MAIN"
 * @property schoolId The unique ID of the main school or university this building belongs to. This is a mandatory link.
 * Example: "SCH001"
 * @property description An optional, brief description of the building's primary purpose or contents.
 * @property numberOfFloors The total number of floors in the building. Nullable if not tracked. Example: 4
 */
data class BuildingDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("campusId") val campusId: String?,
    @SerializedName("schoolId") val schoolId: String,
    @SerializedName("description") val description: String?,
    @SerializedName("numberOfFloors") val numberOfFloors: Int?
)