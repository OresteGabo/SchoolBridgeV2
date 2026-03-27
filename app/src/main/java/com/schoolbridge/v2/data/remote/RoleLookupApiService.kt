package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.user.SchoolLookupDto
import com.schoolbridge.v2.data.dto.user.StudentLookupDto
import com.schoolbridge.v2.data.session.UserSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface RoleLookupApiService {
    suspend fun searchSchools(query: String): List<SchoolLookupDto>
    suspend fun searchStudents(query: String, schoolId: Long? = null): List<StudentLookupDto>
}

class RoleLookupApiServiceImpl(
    private val userSessionManager: UserSessionManager
) : RoleLookupApiService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient {
        expectSuccess = true

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = LogLevel.HEADERS
        }
    }

    override suspend fun searchSchools(query: String): List<SchoolLookupDto> {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for school lookup")

        return runApiCall(defaultMessage = "Could not search schools.") {
            client.get("$BASE_URL/api/schools/search") {
                accept(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                parameter("query", query)
            }.body()
        }
    }

    override suspend fun searchStudents(query: String, schoolId: Long?): List<StudentLookupDto> {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for student lookup")

        return runApiCall(defaultMessage = "Could not search students.") {
            client.get("$BASE_URL/api/students/search") {
                accept(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                parameter("query", query)
                schoolId?.let { parameter("schoolId", it) }
            }.body()
        }
    }
}
