package com.schoolbridge.v2.data.dto.academic

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an **Enrollment**.
 *
 * This DTO represents a student's formal registration in a particular academic program,
 * school, or year. It captures the details of *when* and *where* a student is currently
 * or was previously enrolled.
 *
 * **Real-life Example:**
 * - **Student Profile:** A student's profile might display their "current enrollment"
 * details, showing which academic year, school, and specific program they are in.
 * - **Administrative Record:** When an administrator reviews a student's history, they
 * would see a list of past and current [EnrollmentDto] records.
 *
 * @property id A unique identifier for this enrollment record. Example: "ENRL-ST001-AY2024"
 * @property studentId The ID of the student associated with this enrollment. Example: "ST0054"
 * @property academicYearId The ID of the academic year for which this enrollment is valid.
 * Example: "AY-2024-2025"
 * @property schoolId The ID of the school where the student is enrolled. Example: "SCHL_GHS"
 * @property schoolLevelOfferingId The ID representing the specific program, grade level, or
 * stream the student is enrolled in for this academic year (e.g., "S3 Science, 2024-2025").
 * Example: "SCHL-OFFER-S3SCI-2024"
 * @property enrollmentDate The date when the student's enrollment was officially recorded,
 * as an ISO 8601 date string. Example: "2024-08-15"
 * @property isCurrent A boolean indicating if this is the student's currently active enrollment.
 * Only one enrollment should typically be `true` for a given student at any time.
 * @property gradeIds An optional list of IDs of the specific grades (individual scores)
 * associated with this enrollment. This might be used to group all grades for a specific academic period.
 * @property courseIds An optional list of IDs of the specific courses the student is enrolled in
 * as part of this broader enrollment.
 */
data class EnrollmentDto(
    val id: String,
    @SerializedName("student_id") val studentId: String,
    @SerializedName("academic_year_id") val academicYearId: String,
    @SerializedName("school_id") val schoolId: String,
    @SerializedName("school_level_offering_id") val schoolLevelOfferingId: String,
    @SerializedName("enrollment_date") val enrollmentDate: String,
    @SerializedName("is_current") val isCurrent: Boolean,
    @SerializedName("grade_ids") val gradeIds: List<String>?,
    @SerializedName("course_ids") val courseIds: List<String>?
)