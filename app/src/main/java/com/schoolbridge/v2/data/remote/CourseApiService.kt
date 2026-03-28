package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.academic.MobileCourseFeedDto
import com.schoolbridge.v2.data.session.UserSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface CourseApiService {
    suspend fun getCourses(): MobileCourseFeedDto
}

class CourseApiServiceImpl(
    private val userSessionManager: UserSessionManager
) : CourseApiService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        expectSuccess = true
        install(ContentNegotiation) {
            json(json)
        }
    }

    override suspend fun getCourses(): MobileCourseFeedDto {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for courses request")

        return runApiCall(defaultMessage = "Could not load courses.") {
            client.get("$BASE_URL/mobile/courses") {
                accept(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        }
    }
}
