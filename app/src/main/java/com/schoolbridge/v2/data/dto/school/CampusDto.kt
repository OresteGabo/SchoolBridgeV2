package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.common.AddressDto

/**
 * Data Transfer Object (DTO) for a specific **Campus** of a School or University.
 *
 * This class is used when an educational institution has multiple distinct physical locations. Each campus
 * operates as a semi-independent site under the umbrella of the main school.
 *
 * **When to use this class:**
 * Use `CampusDto` to differentiate facilities, students, or staff across different physical sites of a single institution.
 * This helps manage multi-location operations.
 *
 * **Example Situations:**
 * - **Multi-Campus Management:** A university with a "Main Campus" and a "Satellite Medical Campus."
 * - **Student Registration:** Allowing students to choose which campus they want to enroll in.
 * - **Campus-Specific News/Events:** Displaying news relevant only to a particular campus.
 * - **Resource Allocation:** Managing resources (e.g., libraries, labs) that are unique to each campus.
 *
 * @property id A unique identifier for the campus. Example: "CAMPUS_MAIN"
 * @property name The official name of the campus (e.g., "Main Campus", "Downtown Campus", "Kigali Campus").
 * @property schoolId The unique ID of the main school or university this campus belongs to. This is a mandatory link.
 * Example: "SCH001"
 * @property address The physical address of the campus. This will be a nested [AddressDto] object, containing details like street, city, postal code.
 * @property description An optional, brief description of the campus, its facilities, or its primary focus.
 * @property contactPhone A general contact phone number for the campus. Nullable. Example: "+250788123000"
 * @property contactEmail A general contact email address for the campus. Nullable. Example: "info.main@university.edu"
 */
data class CampusDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("school_id") val schoolId: String,
    @SerializedName("address") val address: AddressDto, // Assuming you have an AddressDto defined
    @SerializedName("description") val description: String?,
    @SerializedName("contact_phone") val contactPhone: String?,
    @SerializedName("contact_email") val contactEmail: String?
)