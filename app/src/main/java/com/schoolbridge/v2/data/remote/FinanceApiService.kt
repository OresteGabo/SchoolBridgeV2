package com.schoolbridge.v2.data.remote

import android.util.Log
import com.schoolbridge.v2.data.dto.finance.MobileFinanceDashboardDto
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

private const val FINANCE_TRACE_TAG = "FINANCE_TRACE"

interface FinanceApiService {
    suspend fun getFinanceDashboard(userId: String): MobileFinanceDashboardDto
}

class FinanceApiServiceImpl(
    private val userSessionManager: UserSessionManager
) : FinanceApiService {
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

        install(Logging) {
            level = LogLevel.HEADERS
        }
    }

    override suspend fun getFinanceDashboard(userId: String): MobileFinanceDashboardDto {
        val url = "$BASE_URL/mobile/finance"
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for finance request")
        Log.d(FINANCE_TRACE_TAG, "FinanceApiService.getFinanceDashboard userId=$userId url=$url")

        return runApiCall(
            defaultMessage = "Could not load finance data.",
            authFailureStatusCodes = authFailureStatusCodes,
            onAuthFailure = { userSessionManager.clearSession() }
        ) {
            client.get(url) {
                accept(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                parameter("userId", userId)
            }.body()
        }
    }
}
