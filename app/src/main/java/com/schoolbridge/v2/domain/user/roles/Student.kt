package com.schoolbridge.v2.domain.user.roles

import com.schoolbridge.v2.domain.academic.Enrollment
import com.schoolbridge.v2.domain.user.NationalExamResult

/**
 * Client-side domain model representing a Student's specific profile data.
 * This class holds data directly relevant to a user when they act as a student.
 * It does NOT contain methods for student actions (e.g., submitting homework),
 * as those are handled by backend API calls.
 *
 * @property userId The ID of the main [User] object this student profile belongs to.
 * @property studentId A unique identifier for this student profile, which might be the same as [userId]
 * or a distinct school-generated ID.
 * @property currentEnrollment The student's current active [Enrollment] in a school or program.
 * This provides details like academic year, school level, etc.
 * @property gradeLevel The student's current academic grade level (e.g., "S3", "Grade 10").
 * @property classStream An optional stream or specialization within their grade (e.g., "Science", "Arts").
 * @property linkedParentUserIds A list of [User] IDs of parents who are officially linked to this student's profile.
 * Used for parental access and communication.
 * @property nationalExamResults A list of [NationalExamResult]s relevant to this student,
 * allowing the UI to display past exam performance.
 *
 * Example Usage:
 * val studentData = user.studentDetails // Assuming user is a student
 * if (studentData != null) {
 * println("Student ${studentData.userId} is in ${studentData.gradeLevel}")
 * println("Current academic year: ${studentData.currentEnrollment?.academicYearId}")
 * }
 */
data class Student(
    val userId: String,
    val studentId: String,
    val currentEnrollment: Enrollment?,
    val gradeLevel: String,
    val classStream: String?,
    val linkedParentUserIds: List<String>,
    val nationalExamResults: List<NationalExamResult>
)