package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto
import com.schoolbridge.v2.data.session.UserSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface MessageApiService {
    suspend fun getMessageThreads(): List<MobileMessageThreadDto>
}

class MessageApiServiceImpl(
    private val userSessionManager: UserSessionManager
) : MessageApiService {

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

    override suspend fun getMessageThreads(): List<MobileMessageThreadDto> {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for message request")

        return client.get("$BASE_URL/mobile/messages") {
            accept(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }
}
