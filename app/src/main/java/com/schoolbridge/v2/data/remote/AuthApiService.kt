package com.schoolbridge.v2.data.remote

import android.os.Build
import com.schoolbridge.v2.data.dto.auth.LoginRequestDto
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.io.IOException

// Dynamically choose BASE_URL based on environment
private val BASE_URL: String
    get() {
        return if (isRunningOnEmulator()) {
            "http://10.0.2.2:8080"
        } else {
            "http://172.20.10.3:8080" // Replace with your real machine IP
        }
    }

private fun isRunningOnEmulator(): Boolean {
    return (Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.lowercase().contains("emulator")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for x86")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
            || "google_sdk" == Build.PRODUCT)
}

interface AuthApiService {
    suspend fun login(request: LoginRequestDto): LoginResponseDto
}

class AuthApiServiceImpl : AuthApiService {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(Logging) {
            level = LogLevel.HEADERS
        }
    }

    override suspend fun login(request: LoginRequestDto): LoginResponseDto {
        return try {
            val response = client.post("$BASE_URL/api/auth/login") {
                contentType(ContentType.Application.Json)
                setBody(request)
            }

            if (response.status == HttpStatusCode.OK) {
                response.body()
            } else {
                val errorBody = response.body<String>()
                throw Exception("Login failed with status ${response.status}: $errorBody")
            }

        } catch (e: ClientRequestException) {
            throw Exception("Client error (${e.response.status}): ${e.message}", e)

        } catch (e: ServerResponseException) {
            throw Exception("Server error (${e.response.status}): ${e.message}", e)

        } catch (e: IOException) {
            throw IOException("Network error. Please check your connection.", e)

        } catch (e: Exception) {
            throw Exception("Unexpected error: ${e.message}", e)
        }
    }
}
