package com.schoolbridge.v2.domain.academic

import java.time.LocalDate

/**
 * Represents a student's Enrollment in a specific academic context.
 * This records which student is in which academic year and school section.
 *
 * @property id Unique identifier for the enrollment record.
 * @property studentId The ID of the [User] (specifically a student) associated with this enrollment.
 * @property academicYearId The ID of the [AcademicYear] this enrollment is for.
 * @property schoolId The ID of the [School] where the student is enrolled.
 * @property schoolLevelOfferingId The ID of the specific [SchoolLevelOffering] (e.g., "Grade 10 Science Section")
 * the student is enrolled in.
 * @property enrollmentDate The date when the student was officially enrolled.
 * @property isCurrent A boolean indicating if this is the student's currently active enrollment.
 * @property courseIds A list of [Course] IDs that the student is enrolled in for this academic period.
 *
 * Example Usage:
 * val studentEnrollment = Enrollment(
 * id = "enrol-std-123-2024",
 * studentId = "user-student-id-abc",
 * academicYearId = "AY2024-2025",
 * schoolId = "school-main",
 * schoolLevelOfferingId = "slo-g10-sci",
 * enrollmentDate = LocalDate.of(2024, 9, 1),
 * isCurrent = true,
 * courseIds = listOf("math-g10-2024", "bio-g10-2024")
 * )
 */
data class Enrollment(
    val id: String,
    val studentId: String,
    val academicYearId: String,
    val schoolId: String, // Assuming School is a domain model in domain.school
    val schoolLevelOfferingId: String,
    val enrollmentDate: LocalDate,
    val isCurrent: Boolean,
    val courseIds: List<String>
)