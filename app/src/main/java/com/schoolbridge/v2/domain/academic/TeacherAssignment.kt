package com.schoolbridge.v2.domain.academic


import com.schoolbridge.v2.domain.school.School
import com.schoolbridge.v2.domain.school.SchoolLevelOffering
import java.time.LocalDate
import java.util.UUID

/**
 * Represents an assignment of a teacher to a specific
 * [SchoolLevelOffering] within a given academic year at a specific school.
 *
 * This defines what the teacher is teaching and where during that period.
 *
 * @property id Unique identifier of this assignment
 * @property teacherId The unique ID of the teacher user
 * @property school The school where the teacher is assigned
 * @property schoolLevelOffering The specific school level offering (e.g. "Senior 4 Science") for this assignment
 * @property academicYear The academic year during which this assignment applies
 * @property startDate Optional actual start date of this assignment (can differ from academic year start)
 * @property endDate Optional actual end date of this assignment (can differ from academic year end)
 * @property isCurrent True if this assignment is the teacher's current active assignment
 * @property assignedCourses List of course IDs or references the teacher is responsible for in this assignment
 */
data class TeacherAssignment(
    val id: String = UUID.randomUUID().toString(),
    val teacherId: String,
    val school: School,
    val schoolLevelOffering: SchoolLevelOffering,
    val academicYear: AcademicYear,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val isCurrent: Boolean = false,
    val assignedCourses: List<String> = emptyList() // course IDs or Course references if you have that class
)
