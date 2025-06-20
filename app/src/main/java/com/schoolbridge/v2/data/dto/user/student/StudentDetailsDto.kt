package com.schoolbridge.v2.data.dto.user.student

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.academic.EnrollmentDto
import com.schoolbridge.v2.data.dto.academic.NationalExamResultDto

/**
 * Data Transfer Object (DTO) for **Student-specific details** as part of a [com.schoolbridge.v2.data.dto.user.UserDto].
 *
 * This DTO contains information relevant only to a user who is registered as a student.
 * It's typically nested within a [com.schoolbridge.v2.data.dto.user.UserDto] when fetching a full user profile.
 *
 * **Real-life Example:**
 * When a student logs in, their [com.schoolbridge.v2.data.dto.user.UserDto] will contain this `StudentDetailsDto` to display
 * their current enrollment status, grade level, and linked parents.
 *
 * @property studentId A unique identifier for the student. Example: "ST0054"
 * @property currentEnrollment An [EnrollmentDto] representing the student's current academic
 * enrollment (e.g., which academic year, combination, or stream). Nullable if not currently enrolled.
 * @property gradeLevel The student's current academic grade or year level. Example: "Grade 10", "Year 2 University"
 * @property classStream The specific class or stream the student belongs to (e.g., "Science A", "Humanities B").
 * Nullable if not applicable or not assigned.
 * @property linkedParentUserIds A list of user IDs for parents or guardians linked to this student's account.
 * Example: `["PRNT001", "PRNT002"]`
 * @property nationalExamResults A list of [NationalExamResultDto] objects representing the student's
 * results from national examinations (e.g., High School Leaving Exams). Nullable if not applicable.
 */
data class StudentDetailsDto(
    @SerializedName("studentId") val studentId: String,
    @SerializedName("currentEnrollment") val currentEnrollment: EnrollmentDto?,
    @SerializedName("gradeLevel") val gradeLevel: String,
    @SerializedName("classStream") val classStream: String?,
    @SerializedName("linked_parent_user_ids") val linkedParentUserIds: List<String>,
    @SerializedName("national_exam_results") val nationalExamResults: List<NationalExamResultDto>?
)