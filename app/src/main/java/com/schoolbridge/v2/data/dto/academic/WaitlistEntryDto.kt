package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Waitlist Entry**.
 *
 * This DTO represents a student's entry on a waitlist for a specific school level, section,
 * or academic program, as exchanged with the backend API. It tracks their position and status.
 *
 * @property id A unique identifier for this waitlist entry. Example: "WL_ENTRY_001"
 * @property studentId The ID of the student on the waitlist. Example: "STUDENT005"
 * @property schoolId The ID of the school the waitlist is for. Example: "SCH001"
 * @property targetSchoolLevelOfferingId The ID of the [SchoolLevelOfferingDto] the student is waitlisted for. Example: "PRIMARY_OFFERING_XYZ"
 * @property targetSchoolSectionId An optional ID of a specific [SchoolSectionDto] within the target level. Nullable. Example: "PRIMARY_GRADE_3"
 * @property academicYearId The ID of the [AcademicYearDto] the student is waitlisted for. Example: "ACAYEAR_2025_2026"
 * @property applicationDate The date and time when the student was added to the waitlist, as an ISO 8601 datetime string. Example: "2025-01-15T10:00:00Z"
 * @property status The current status of the waitlist entry (e.g., "PENDING", "OFFER_MADE", "ACCEPTED", "DECLINED", "REMOVED"). Example: "PENDING"
 * @property waitlistPriority A numerical or categorical indicator of the student's priority on the waitlist (e.g., 1, "HIGH", "SIBLING_OF_CURRENT_STUDENT"). Nullable.
 * @property notes Any additional notes or comments related to this waitlist entry. Nullable.
 */
data class WaitlistEntryDto(
    @SerializedName("id") val id: String,
    @SerializedName("student_id") val studentId: String,
    @SerializedName("school_id") val schoolId: String,
    @SerializedName("target_school_level_offering_id") val targetSchoolLevelOfferingId: String,
    @SerializedName("target_school_section_id") val targetSchoolSectionId: String?,
    @SerializedName("academic_year_id") val academicYearId: String,
    @SerializedName("application_date") val applicationDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("waitlist_priority") val waitlistPriority: String?, // Could be Int if purely numerical
    @SerializedName("notes") val notes: String?
)