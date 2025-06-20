package com.schoolbridge.v2.domain.academic

// Extend your Course with a dummy flag for finished + validated (since not in your class)
data class CourseWithStatus(
    val course: Course,
    val status: CourseStatus
)