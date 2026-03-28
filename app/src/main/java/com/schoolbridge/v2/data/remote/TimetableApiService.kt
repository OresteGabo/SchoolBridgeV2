package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.academic.MobileTimetableResponseDto
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

interface TimetableApiService {
    suspend fun getTimetable(): MobileTimetableResponseDto
}

class TimetableApiServiceImpl(
    private val userSessionManager: UserSessionManager
) : TimetableApiService {
    private val authFailureStatusCodes = setOf(401, 403)

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

    override suspend fun getTimetable(): MobileTimetableResponseDto {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for timetable request")

        return runApiCall(
            defaultMessage = "Could not load timetable.",
            authFailureStatusCodes = authFailureStatusCodes,
            onAuthFailure = { userSessionManager.clearSession() }
        ) {
            client.get("$BASE_URL/mobile/timetable") {
                accept(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        }
    }
}
