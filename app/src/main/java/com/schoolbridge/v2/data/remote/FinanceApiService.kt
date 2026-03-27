package com.schoolbridge.v2.data.remote

import android.util.Log
import com.schoolbridge.v2.data.dto.finance.MobileFinanceDashboardDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

private const val FINANCE_TRACE_TAG = "FINANCE_TRACE"

interface FinanceApiService {
    suspend fun getFinanceDashboard(userId: String): MobileFinanceDashboardDto
}

class FinanceApiServiceImpl : FinanceApiService {

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
        Log.d(FINANCE_TRACE_TAG, "FinanceApiService.getFinanceDashboard userId=$userId url=$url")

        return client.get(url) {
            accept(ContentType.Application.Json)
            parameter("userId", userId)
        }.body()
    }
}
