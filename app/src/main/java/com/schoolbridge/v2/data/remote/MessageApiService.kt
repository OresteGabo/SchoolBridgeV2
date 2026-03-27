package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.message.CreateMessageRequestDto
import com.schoolbridge.v2.data.dto.message.MarkMessageReadRequestDto
import com.schoolbridge.v2.data.dto.message.MobileMessageThreadDto
import com.schoolbridge.v2.data.session.UserSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.accept
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

interface MessageApiService {
    suspend fun getMessageThreads(): List<MobileMessageThreadDto>
    suspend fun sendMessage(conversationId: Long, senderId: Long, content: String)
    suspend fun markMessageAsRead(messageId: Long, userId: Long)
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

        return runApiCall(defaultMessage = "Could not load messages.") {
            client.get("$BASE_URL/mobile/messages") {
                accept(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        }
    }

    override suspend fun sendMessage(conversationId: Long, senderId: Long, content: String) {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for message request")

        runApiCall(defaultMessage = "Could not send your message.") {
            client.post("$BASE_URL/messages") {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(
                    CreateMessageRequestDto(
                        conversationId = conversationId,
                        senderId = senderId,
                        content = content
                    )
                )
            }
        }
    }

    override suspend fun markMessageAsRead(messageId: Long, userId: Long) {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for message request")

        runApiCall(defaultMessage = "Could not update the message status.") {
            client.post("$BASE_URL/messages/$messageId/read") {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(MarkMessageReadRequestDto(userId = userId))
            }
        }
    }
}
