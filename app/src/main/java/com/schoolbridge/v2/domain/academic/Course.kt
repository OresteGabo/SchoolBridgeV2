package com.schoolbridge.v2.domain.academic

import com.schoolbridge.v2.domain.school.SchoolLevelOffering // Assuming this will exist
import java.time.LocalDate

/**
 * Represents a Course offered in the academic system.
 * A course is a specific subject taught within a particular academic context.
 *
 * @property id Unique identifier for the course.
 * @property name The name of the course (e.g., "Biology S3", "Mathematics Grade 10").
 * @property description A detailed description of the course content and objectives.
 * @property subjectId The ID of the underlying [Subject] (e.g., "Biology", "Mathematics").
 * @property academicYearId The ID of the [AcademicYear] in which this course is offered.
 * @property schoolLevelOfferingId The ID of the [SchoolLevelOffering] (e.g., S3 Science section)
 * that this course is part of, linking it to a specific academic context within a school.
 * @property teacherUserIds A list of [User] IDs (specifically teachers) assigned to teach this course.
 * @property startDate The official start date of the course.
 * @property endDate The official end date of the course.
 * @property isActive A boolean indicating if the course is currently active and open for enrollment/activity.
 *
 * Example Usage:
 * val mathCourse = Course(
 * id = "math-g10-2024",
 * name = "Mathematics Grade 10",
 * description = "Comprehensive study of algebra and geometry.",
 * subjectId = "subj-math",
 * academicYearId = "AY2024-2025",
 * schoolLevelOfferingId = "slo-g10-sci",
 * teacherUserIds = listOf("user-teacher-id-123"),
 * startDate = LocalDate.of(2024, 9, 1),
 * endDate = LocalDate.of(2025, 5, 30),
 * isActive = true
 * )
 */
data class Course(
    val id: String,
    val name: String,
    val description: String?,
    val subjectId: String,
    val academicYearId: String,
    val schoolLevelOfferingId: String, // e.g., ID for "S3 Science, 2024-2025"
    val teacherUserIds: List<String>,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val isActive: Boolean
)