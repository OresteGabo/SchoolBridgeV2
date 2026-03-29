package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.academic.CreatePersonalTimetablePlanRequestDto
import com.schoolbridge.v2.data.dto.academic.MobilePersonalTimetablePlanDto
import com.schoolbridge.v2.data.dto.academic.MobileTimetableResponseDto
import com.schoolbridge.v2.data.session.UserSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface TimetableApiService {
    suspend fun getTimetable(): MobileTimetableResponseDto
    suspend fun createPersonalPlan(request: CreatePersonalTimetablePlanRequestDto): MobilePersonalTimetablePlanDto
    suspend fun updatePersonalPlan(planId: Long, request: CreatePersonalTimetablePlanRequestDto): MobilePersonalTimetablePlanDto
    suspend fun deletePersonalPlan(planId: Long)
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

    override suspend fun createPersonalPlan(request: CreatePersonalTimetablePlanRequestDto): MobilePersonalTimetablePlanDto {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for timetable request")

        return runApiCall(
            defaultMessage = "Could not create your personal plan.",
            authFailureStatusCodes = authFailureStatusCodes,
            onAuthFailure = { userSessionManager.clearSession() }
        ) {
            client.post("$BASE_URL/mobile/timetable/personal-plans") {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(request)
            }.body()
        }
    }

    override suspend fun updatePersonalPlan(planId: Long, request: CreatePersonalTimetablePlanRequestDto): MobilePersonalTimetablePlanDto {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for timetable request")

        return runApiCall(
            defaultMessage = "Could not update your personal plan.",
            authFailureStatusCodes = authFailureStatusCodes,
            onAuthFailure = { userSessionManager.clearSession() }
        ) {
            client.put("$BASE_URL/mobile/timetable/personal-plans/$planId") {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(request)
            }.body()
        }
    }

    override suspend fun deletePersonalPlan(planId: Long) {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for timetable request")

        runApiCall(
            defaultMessage = "Could not delete your personal plan.",
            authFailureStatusCodes = authFailureStatusCodes,
            onAuthFailure = { userSessionManager.clearSession() }
        ) {
            client.delete("$BASE_URL/mobile/timetable/personal-plans/$planId") {
                accept(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
}
