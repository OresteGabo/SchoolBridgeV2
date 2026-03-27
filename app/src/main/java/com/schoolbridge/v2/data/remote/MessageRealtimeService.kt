package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.message.MessagingRealtimeEventDto
import com.schoolbridge.v2.data.session.UserSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.json.json
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

interface MessageRealtimeService {
    fun observeEvents(): Flow<MessagingRealtimeEventDto>
}

class MessageRealtimeServiceImpl(
    private val userSessionManager: UserSessionManager
) : MessageRealtimeService {

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(json)
        }
        install(WebSockets)
    }

    override fun observeEvents(): Flow<MessagingRealtimeEventDto> = callbackFlow {
        val token = userSessionManager.getAuthTokenSync()
            ?: run {
                close(IllegalStateException("Missing auth token for messaging websocket"))
                return@callbackFlow
            }

        val session = withContext(Dispatchers.IO) {
            client.webSocketSession(
                urlString = "${BASE_URL.toWebSocketBaseUrl()}/ws/messages?token=$token"
            )
        }

        val collector = launch {
            for (frame in session.incoming) {
                if (frame is Frame.Text) {
                    runCatching {
                        json.decodeFromString<MessagingRealtimeEventDto>(frame.readText())
                    }.onSuccess { event ->
                        trySend(event)
                    }
                }
            }
        }

        awaitClose {
            collector.cancel()
            launch {
                runCatching {
                    session.close()
                }
            }
        }
    }
}

private fun String.toWebSocketBaseUrl(): String = when {
    startsWith("https://") -> replaceFirst("https://", "wss://")
    startsWith("http://") -> replaceFirst("http://", "ws://")
    else -> this
}
