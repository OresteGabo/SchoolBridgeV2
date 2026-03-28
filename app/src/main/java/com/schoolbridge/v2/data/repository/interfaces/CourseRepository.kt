package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.academic.MobileCourseFeedDto

interface CourseRepository {
    suspend fun getCourses(): MobileCourseFeedDto
}
