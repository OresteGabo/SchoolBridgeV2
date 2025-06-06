package com.schoolbridge.v2.data.dto.user

import com.google.gson.annotations.SerializedName
import com.schoolbridge.v2.data.dto.academic.CourseDto // Assuming CourseDto is in data.dto.academic
import com.schoolbridge.v2.data.dto.school.CollegeDto // <-- Corrected Import


/**
 * Data Transfer Object (DTO) for **Teacher-specific details** as part of a [UserDto].
 *
 * This DTO holds all the information unique to a user who has the "TEACHER" role.
 * It's typically nested within a [UserDto] when fetching a full user profile.
 *
 * **Real-life Example:**
 * When a school administrator views a teacher's profile, the backend might return a [UserDto]
 * that includes these `TeacherDetailsDto` fields to show their assignments, experience, etc.
 *
 * @property teacherId A unique identifier for the teacher. Example: "TCHR001"
 * @property staffId The official staff ID assigned by the institution. Example: "UR-STAFF-12345"
 * @property assignedCourses A list of [CourseDto] objects representing the courses this teacher is
 * currently assigned to teach.
 * Example: `[CourseDto("ENG101", "English Literature"), CourseDto("HIST203", "World History")]`
 * @property college The [CollegeDto] this teacher is primarily affiliated with. This is relevant for
 * universities (e.g., College of Arts and Social Sciences). It's nullable as not all schools
 * or teachers might have this affiliation.
 * Example: `CollegeDto(id="CBE", name="College of Business and Economics", ...)`
 * @property yearsOfExperience The total number of years the teacher has been teaching. Example: 10
 * @property qualification The highest academic qualification obtained by the teacher.
 * Example: "PhD in Education", "Master of Science in Physics"
 */
data class TeacherDetailsDto(
    @SerializedName("teacher_id") val teacherId: String,
    @SerializedName("staff_id") val staffId: String,
    @SerializedName("assigned_courses") val assignedCourses: List<CourseDto>,
    @SerializedName("college") val college: CollegeDto?,
    @SerializedName("years_of_experience") val yearsOfExperience: Int,
    @SerializedName("qualification") val qualification: String
)