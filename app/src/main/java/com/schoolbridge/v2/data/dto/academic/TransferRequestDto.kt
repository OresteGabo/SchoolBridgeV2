package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for a **Student Transfer Request**.
 *
 * This DTO represents a formal request for a student to transfer between schools,
 * or within the same school but to a different program/level/section, as exchanged
 * with the backend API.
 *
 * @property id A unique identifier for this transfer request. Example: "TR_REQ_001"
 * @property studentId The ID of the student for whom the transfer is requested. Example: "STUDENT003"
 * @property currentSchoolId The ID of the student's current school. Example: "SCH001"
 * @property targetSchoolId The ID of the school the student wishes to transfer to. Can be the same as current for internal transfers. Example: "SCH002"
 * @property currentEnrollmentId The ID of the student's current [EnrollmentDto] being transferred from. Nullable if new enrollment.
 * @property targetAcademicYearId The ID of the [AcademicYearDto] for the desired transfer. Example: "ACAYEAR_2025_2026"
 * @property targetSchoolLevelOfferingId The ID of the [SchoolLevelOfferingDto] the student wishes to transfer to within the target school. Nullable.
 * @property targetSchoolSectionId An optional ID of a specific [SchoolSectionDto] the student wishes to transfer to. Nullable.
 * @property requestDate The date and time when the transfer request was submitted, as an ISO 8601 datetime string. Example: "2025-05-20T09:30:00Z"
 * @property status The current status of the transfer request (e.g., "PENDING", "APPROVED", "REJECTED", "CANCELLED", "COMPLETED"). Example: "PENDING"
 * @property reasonForTransfer A brief description of the reason for the transfer. Example: "Relocation to new city."
 * @property requestedByUserId The ID of the user (e.g., parent, administrator) who submitted the request. Example: "USER_PARENT001"
 * @property approvalDate The date and time when the request was approved or rejected. Nullable. Example: "2025-06-05T14:00:00Z"
 * @property approvalNotes Any notes from the approver or school administration. Nullable.
 */
data class TransferRequestDto(
    @SerializedName("id") val id: String,
    @SerializedName("student_id") val studentId: String,
    @SerializedName("current_school_id") val currentSchoolId: String,
    @SerializedName("target_school_id") val targetSchoolId: String,
    @SerializedName("current_enrollment_id") val currentEnrollmentId: String?,
    @SerializedName("target_academic_year_id") val targetAcademicYearId: String,
    @SerializedName("target_school_level_offering_id") val targetSchoolLevelOfferingId: String?,
    @SerializedName("target_school_section_id") val targetSchoolSectionId: String?,
    @SerializedName("request_date") val requestDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("reason_for_transfer") val reasonForTransfer: String,
    @SerializedName("requested_by_user_id") val requestedByUserId: String,
    @SerializedName("approval_date") val approvalDate: String?,
    @SerializedName("approval_notes") val approvalNotes: String?
)