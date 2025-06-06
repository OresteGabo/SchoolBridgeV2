package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.academic.EnrollmentDto // Assuming EnrollmentDto is in data.dto.academic
import com.schoolbridge.v2.data.dto.academic.NationalExamResultDto // Assuming this DTO exists

/**
 * Data Transfer Object (DTO) for **Student-specific details** as part of a [UserDto].
 *
 * This DTO contains information relevant only to a user who is registered as a student.
 * It's typically nested within a [UserDto] when fetching a full user profile.
 *
 * **Real-life Example:**
 * When a student logs in, their [UserDto] will contain this `StudentDetailsDto` to display
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
    @SerializedName("student_id") val studentId: String,
    @SerializedName("current_enrollment") val currentEnrollment: EnrollmentDto?,
    @SerializedName("grade_level") val gradeLevel: String,
    @SerializedName("class_stream") val classStream: String?,
    @SerializedName("linked_parent_user_ids") val linkedParentUserIds: List<String>,
    @SerializedName("national_exam_results") val nationalExamResults: List<NationalExamResultDto>?
)