package com.schoolbridge.v2.data.remote

import android.os.Build
import android.util.Log
import com.schoolbridge.v2.data.dto.auth.LoginRequestDto
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Headers
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.io.IOException
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

private const val AUTH_TRACE_TAG = "AUTH_TRACE"

// Dynamically choose BASE_URL based on environment
val BASE_URL: String
    get() {
        val configuredBaseUrl = NetworkConfig.apiBaseUrl.trim().trimEnd('/')

        return when {
            configuredBaseUrl.isNotBlank() -> configuredBaseUrl
            isRunningOnEmulator() -> {
            "http://10.0.2.2:8080"
            }
            else -> {
                // Real devices cannot reach your laptop through 127.0.0.1.
                // Set api.baseUrl in local.properties to your computer's reachable LAN IP.
                "http://172.20.10.4:8080"
            }
        }
    }


private fun isRunningOnEmulator(): Boolean {
    val fingerprint = Build.FINGERPRINT
    val model = Build.MODEL
    val product = Build.PRODUCT

    return (fingerprint.startsWith("generic")
            || fingerprint.startsWith("google/sdk_gphone") // Add this!
            || fingerprint.lowercase().contains("emulator")
            || model.contains("Emulator")
            || model.contains("Android SDK built for x86")
            || model.contains("sdk_gphone64_arm64") // Your specific emulator model
            || Build.MANUFACTURER.contains("Genymotion")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
            || "google_sdk" == product
            || "sdk_gphone64_arm64" == product)
}
interface AuthApiService {
    suspend fun login(request: LoginRequestDto): LoginResult
}

sealed interface LoginResult {
    data class Success(val response: LoginResponseDto) : LoginResult
    data class Failure(val message: String, val statusCode: Int? = null) : LoginResult
}

class AuthApiServiceImpl : AuthApiService {

    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    private val client = HttpClient {
        expectSuccess = false

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = LogLevel.HEADERS
        }
    }

    override suspend fun login(request: LoginRequestDto): LoginResult {
        val TAG = "LoginRepo"
        val url = "$BASE_URL/api/auth/login"
        val runningOnEmulator = isRunningOnEmulator()
        val requestJson = json.encodeToString(
            buildJsonObject {
                put("username", request.username)
                put("password", request.password)
            }
        )
        val sanitizedRequestJson = requestJson.replace(request.password, "***")

        return try {
            Log.d(
                AUTH_TRACE_TAG,
                "AuthApiService.login:start url=$url emulator=$runningOnEmulator fingerprint=${Build.FINGERPRINT} model=${Build.MODEL} product=${Build.PRODUCT}"
            )
            Log.d(
                AUTH_TRACE_TAG,
                "AuthApiService.login:request summary username='${request.username}' usernameLength=${request.username.length} passwordLength=${request.password.length}"
            )
            Log.d(
                AUTH_TRACE_TAG,
                "AuthApiService.login:request headers Accept=${ContentType.Application.Json} Content-Type=${ContentType.Application.Json}"
            )
            Log.d(AUTH_TRACE_TAG, "AuthApiService.login:request body=$sanitizedRequestJson")
            Log.d(TAG, "Sending login request to $url")
            Log.d(TAG, "Login request payload=$sanitizedRequestJson")

            val response: HttpResponse = client.post(url) {
                accept(ContentType.Application.Json)
                contentType(ContentType.Application.Json)
                setBody(TextContent(requestJson, ContentType.Application.Json))
            }

            val responseBody = response.bodyAsText()
            val sanitizedResponseBody = sanitizeResponseBody(responseBody)

            Log.d(
                TAG,
                "Received response with status=${response.status}, contentType=${response.headers["Content-Type"]}"
            )
            Log.d(
                AUTH_TRACE_TAG,
                "AuthApiService.login:response status=${response.status.value} contentType=${response.headers["Content-Type"]}"
            )
            Log.d(
                AUTH_TRACE_TAG,
                "AuthApiService.login:response headers=${formatHeaders(response.headers)}"
            )
            Log.d(
                AUTH_TRACE_TAG,
                "AuthApiService.login:response body=$sanitizedResponseBody"
            )

            if (response.status == HttpStatusCode.OK) {
                val body: LoginResponseDto = json.decodeFromString(responseBody)
                Log.d(
                    AUTH_TRACE_TAG,
                    "AuthApiService.login:success tokenType=${body.tokenType} expiresInSeconds=${body.expiresInSeconds} userId=${body.user.id} roles=${body.user.roles}"
                )
                Log.d(TAG, "Login success. Response body=$body")
                LoginResult.Success(body)
            } else {
                Log.w(TAG, "Login refused. Status=${response.status}, Body=$responseBody")
                val extractedMessage = extractErrorMessage(responseBody)
                Log.d(
                    AUTH_TRACE_TAG,
                    "AuthApiService.login:failure parsedMessage=${extractedMessage ?: "<null>"} status=${response.status.value}"
                )
                LoginResult.Failure(
                    message = extractedMessage
                        ?: "Login failed with status ${response.status.value}",
                    statusCode = response.status.value
                )
            }

        } catch (e: ClientRequestException) {
            val errorBody = runCatching { e.response.bodyAsText() }.getOrNull()
            Log.w(
                AUTH_TRACE_TAG,
                "AuthApiService.login:ClientRequestException status=${e.response.status.value} body=${sanitizeResponseBody(errorBody)} message=${e.message}"
            )
            Log.w(TAG, "ClientRequestException: ${e.message}, body=$errorBody")
            LoginResult.Failure(
                message = extractErrorMessage(errorBody) ?: "Client error (${e.response.status.value})",
                statusCode = e.response.status.value
            )

        } catch (e: ServerResponseException) {
            val errorBody = runCatching { e.response.bodyAsText() }.getOrNull()
            Log.e(
                AUTH_TRACE_TAG,
                "AuthApiService.login:ServerResponseException status=${e.response.status.value} body=${sanitizeResponseBody(errorBody)} message=${e.message}",
                e
            )
            Log.e(TAG, "ServerResponseException: ${e.message}, body=$errorBody", e)
            LoginResult.Failure(
                message = extractErrorMessage(errorBody) ?: "Server error (${e.response.status.value})",
                statusCode = e.response.status.value
            )

        } catch (e: IOException) {
            Log.e(AUTH_TRACE_TAG, "AuthApiService.login:IOException message=${e.message}", e)
            Log.e(TAG, "IOException: ${e.message}", e)
            throw IOException("Network error. Please check your connection.", e)

        } catch (e: Exception) {
            Log.e(AUTH_TRACE_TAG, "AuthApiService.login:UnexpectedException message=${e.message}", e)
            Log.e(TAG, "Unexpected exception: ${e.message}", e)
            throw Exception("Unexpected error: ${e.message}", e)
        }
    }

    private fun extractErrorMessage(errorBody: String?): String? {
        if (errorBody.isNullOrBlank()) return null

        return runCatching {
            json.parseToJsonElement(errorBody)
                .jsonObject["message"]
                ?.jsonPrimitive
                ?.content
        }.getOrNull()
    }

    private fun formatHeaders(headers: Headers): String =
        headers.entries()
            .joinToString(prefix = "[", postfix = "]") { (key, values) ->
                "$key=${values.joinToString("|")}"
            }

    private fun sanitizeResponseBody(body: String?): String? {
        if (body == null) return null
        return body.replace(Regex("\"token\"\\s*:\\s*\"[^\"]+\""), "\"token\":\"***\"")
    }
}
