package com.schoolbridge.v2.domain.user.roles

import com.schoolbridge.v2.domain.academic.Enrollment
import com.schoolbridge.v2.domain.school.SchoolLevelOffering
import com.schoolbridge.v2.domain.user.NationalExamResult

/**
 * Client-side domain model representing a Student's academic profile details.
 * This class contains information specific to the user when acting in the student role.
 * It does not include behavior or actions like homework submissions,
 * which are managed via backend APIs.
 *
 * @property userId The unique identifier of the main [com.schoolbridge.v2.domain.user.User] this student profile is associated with.
 * This ID can be used to fetch general user information such as name and contact details.
 * @property currentEnrollment The student's active [Enrollment] record,
 * detailing their current academic year, school, and level offering.
 * @property currentSchoolLevelOffering The [SchoolLevelOffering] that the student is currently attending,
 * providing direct access to the school, level, academic year, and offered courses.
 * @property gradeLevel Optional textual representation of the student's grade level (e.g., "S3", "Grade 10").
 * Useful for quick display purposes without traversing enrollments.
 *
 * Example usage:
 * ```
 * val student = user.studentDetails
 * if (student != null) {
 *   println("Student ID: ${student.userId}, Grade: ${student.gradeLevel}")
 *   println("Enrolled in academic year: ${student.currentEnrollment.academicYearId}")
 *   println("Courses offered: ${student.currentSchoolLevelOffering.courses.map { it.name }}")
 * }
 * ```
 */
data class Student(
    val userId: String,
    val currentEnrollment: Enrollment,
    val currentSchoolLevelOffering: SchoolLevelOffering,
    val gradeLevel: String?
)


