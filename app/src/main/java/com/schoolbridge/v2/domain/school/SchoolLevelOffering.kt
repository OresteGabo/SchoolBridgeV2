package com.schoolbridge.v2.domain.school

import com.schoolbridge.v2.domain.academic.AcademicYear
import com.schoolbridge.v2.domain.academic.Course
import java.util.UUID

/**
 * Represents the specific offering of a school level by a particular school during a defined academic year.
 *
 * This entity models what students enroll in at a school, uniquely identifying the combination
 * of school, level, academic year, and optionally a stream. It also contains the list of courses
 * provided as part of this offering.
 *
 * Example:
 * - School: Green Hills Academy
 * - School Level: Senior 4
 * - Academic Year: 2024–2025
 * - Stream: "Science"
 * - Courses: Mathematics, Physics, Chemistry, Biology
 *
 * @property id A unique identifier for this specific school level offering.
 * Defaults to a randomly generated UUID if not provided.
 *
 * @property school The [School] entity providing this level offering.
 *
 * @property schoolLevel The [SchoolLevel] representing the general educational level,
 * such as "Senior 4" or "Primary 2".
 *
 * @property academicYear The [AcademicYear] during which this offering is valid.
 *
 * @property stream Optional subdivision or specialization within the level,
 * for example "A", "B", or "Science".
 *
 * @property courses A list of [Course]s included in this school level offering.
 * These define the subjects taught for this level during the academic year.
 */
data class SchoolLevelOffering(
    val id: String = UUID.randomUUID().toString(),
    val school: School,
    val schoolLevel: SchoolLevel,
    val academicYear: AcademicYear,
    val stream: String? = null,
    val courses: List<Course>
)
