package com.schoolbridge.v2.data.dto.school

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for an academic **Department** within a School or College.
 *
 * This represents a specific academic area, such as the "Mathematics Department," "Computer Science Department,"
 * or "History Department." Departments typically reside within a Faculty or directly under a School's administration.
 *
 * **When to use this class:**
 * Use `DepartmentDto` when your application needs to display, manage, or organize information at the departmental level.
 * This is crucial for managing courses, teachers, and students specific to an academic discipline.
 *
 * **Example Situations:**
 * - **Course Catalog:** Displaying courses offered by specific departments.
 * - **Teacher Assignments:** Assigning teachers to a particular department.
 * - **Student Enrollment by Major:** Tracking which students are enrolled in specific departmental programs.
 * - **Head of Department Management:** Linking a user to their role as Head of Department.
 *
 * @property id A unique identifier for the department. Example: "DEPT_CS"
 * @property name The full name of the department. Example: "Department of Computer Science"
 * @property description An optional, brief description of the department's focus or academic areas.
 * @property schoolId The unique ID of the school or university this department belongs to. This is a mandatory link.
 * Example: "SCH001"
 * @property collegeId An optional unique ID of the College or Faculty this department belongs to.
 * This is used in larger institutions (like universities) where departments are nested within colleges/faculties.
 * Nullable if the department reports directly to a school without an intermediate college. Example: "FAC001"
 * @property headOfDepartmentUserId The unique ID of the user who is currently the Head of this Department (HoD).
 * Nullable if the HoD position is vacant or not tracked in the system. Example: "usr_hod_cs"
 */
data class DepartmentDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("school_id") val schoolId: String,
    @SerializedName("college_id") val collegeId: String?,
    @SerializedName("head_of_department_user_id") val headOfDepartmentUserId: String?
)