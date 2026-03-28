package com.schoolbridge.v2.data.repository.implementations

import com.schoolbridge.v2.data.dto.academic.MobileCourseFeedDto
import com.schoolbridge.v2.data.remote.CourseApiService
import com.schoolbridge.v2.data.repository.interfaces.CourseRepository

class CourseRepositoryImpl(
    private val courseApiService: CourseApiService
) : CourseRepository {
    override suspend fun getCourses(): MobileCourseFeedDto = courseApiService.getCourses()
}
