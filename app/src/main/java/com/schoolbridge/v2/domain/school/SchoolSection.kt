package com.schoolbridge.v2.domain.school

import com.schoolbridge.v2.domain.academic.Course

/**
 * Represents a section or stream within a school curriculum, such as MCB or HEG in A-Level.
 * Each section groups together a list of main courses that students in that section typically take.
 *
 * @property name Full name of the section, e.g. "Mathematics, Chemistry, Biology"
 * @property abbrevName Abbreviated name for the section, e.g. "MCB"
 * @property mainCourses List of courses that are main subjects in this section
 */
data class SchoolSection(
    val name: String,
    val abbrevName: String,
    val mainCourses: List<Course>?= emptyList()
)