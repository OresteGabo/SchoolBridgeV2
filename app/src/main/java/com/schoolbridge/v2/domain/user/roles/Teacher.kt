package com.schoolbridge.v2.domain.user.roles

import com.schoolbridge.v2.domain.academic.Course
import com.schoolbridge.v2.domain.academic.TeacherAssignment
import com.schoolbridge.v2.domain.school.Faculty


/**
 * Represents teacher-specific details including all teaching assignments over time.
 *
 * @property teacherId The unique ID of the teacher user.
 * @property assignments List of all [TeacherAssignment]s representing each teaching period.
 */
data class Teacher(
    val teacherId: String,
    val assignments: List<TeacherAssignment>
) {
    /**
     * The currently active assignment, or null if none.
     */
    val currentAssignment: TeacherAssignment?
        get() = assignments.find { it.isCurrent }
}