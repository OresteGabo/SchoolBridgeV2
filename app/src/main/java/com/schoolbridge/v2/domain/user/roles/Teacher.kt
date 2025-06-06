package com.schoolbridge.v2.domain.user.roles

import com.schoolbridge.v2.domain.academic.Course
import com.schoolbridge.v2.domain.school.Faculty

/**
 * Client-side domain model representing a Teacher's specific profile data.
 * This class holds data directly relevant to a user when they act as a teacher.
 * It does NOT contain methods for teacher actions (e.g., assigning grades),
 * as those are handled by backend API calls.
 *
 * @property userId The ID of the main [User] object this teacher profile belongs to.
 * @property teacherId A unique identifier for this teacher profile, which might be the same as [userId]
 * or a distinct school-generated staff ID.
 * @property staffId The school-specific staff identifier for the teacher.
 * @property assignedCourses A list of [Course] domain models that this teacher is currently assigned to teach.
 * Used for displaying their timetable or subject responsibilities.
 * @property faculty The [Faculty] (e.g., "Science Department") this teacher belongs to.
 * @property yearsOfExperience The number of years the teacher has been teaching.
 * @property qualification The highest educational qualification of the teacher (e.g., "Masters in Education").
 *
 * Example Usage:
 * val teacherData = user.teacherDetails // Assuming user is a teacher
 * if (teacherData != null) {
 * println("Teacher ${teacherData.userId} teaches: ${teacherData.assignedCourses.joinToString { it.name }}")
 * println("Years of experience: ${teacherData.yearsOfExperience}")
 * }
 */
data class Teacher(
    val userId: String,
    val teacherId: String,
    val staffId: String,
    val assignedCourses: List<Course>,
    val faculty: Faculty?,
    val yearsOfExperience: Int,
    val qualification: String
)