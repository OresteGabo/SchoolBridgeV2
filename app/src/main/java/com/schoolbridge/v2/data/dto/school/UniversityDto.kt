package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **University or Higher Education Institution**.
 *
 * This DTO encapsulates the key information for a university or a tertiary education provider,
 * including its accreditation, location, and types of degrees offered.
 *
 * **When to use this class:**
 * You'll typically use `UniversityDto` when:
 * 1.  **Displaying university information:** Showing details of higher education institutions (e.g., for career counseling or partner universities).
 * 2.  **Managing university records:** An administrator maintains a database of universities.
 * 3.  **Student applications:** Facilitating student applications to various universities.
 *
 * **How to use it:**
 * This DTO would be a standalone entity that might be linked from a student's profile (e.g., alumni association)
 * or a school's career guidance section.
 *
 * @property id A unique identifier for the university. Example: "UNIV_RWANDA_001"
 * @property name The full name of the university. Example: "University of Rwanda"
 * @property shortName A common short name or acronym for the university. Nullable. Example: "UR"
 * @property websiteUrl The official website URL of the university. Nullable. Example: "https://www.ur.ac.rw"
 * @property location A string describing the university's primary location (e.g., "Kigali, Rwanda").
 * This could also be a nested LocationDto for more detail. Example: "Kigali, Rwanda"
 * @property type A string indicating the type of university (e.g., "PUBLIC", "PRIVATE", "TECHNICAL"). Example: "PUBLIC"
 * @property accreditationStatus The current accreditation status of the university (e.g., "FULLY_ACCREDITED", "PROVISIONAL"). Example: "FULLY_ACCREDITED"
 * @property primaryContactEmail The main contact email for the university. Nullable.
 * @property offeredDegreeLevels A list of strings indicating the levels of degrees offered (e.g., "BACHELOR", "MASTER", "PHD").
 * Example: `["BACHELOR", "MASTER", "PHD"]`
 * @property keyFaculties A list of strings naming the prominent faculties or colleges within the university. Nullable.
 * Example: `["Engineering", "Medicine", "Social Sciences"]`
 */
data class UniversityDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("short_name") val shortName: String?,
    @SerializedName("website_url") val websiteUrl: String?,
    @SerializedName("location") val location: String, // Could be a nested LocationDto
    @SerializedName("type") val type: String,
    @SerializedName("accreditation_status") val accreditationStatus: String,
    @SerializedName("primary_contact_email") val primaryContactEmail: String?,
    @SerializedName("offered_degree_levels") val offeredDegreeLevels: List<String>,
    @SerializedName("key_faculties") val keyFaculties: List<String>?
)