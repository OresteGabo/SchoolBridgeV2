package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.auth.LoginRequestDto
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Define your backend URL (replace with your actual IP if running on device)
const val BASE_URL = "http://10.0.2.2:8080"
//const val BASE_URL = "http://172.20.10.3:8080" //for real device

interface AuthApiService {
    suspend fun login(request: LoginRequestDto): LoginResponseDto
}

class AuthApiServiceImpl : AuthApiService {

    // Ktor HTTP client configured for JSON
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                // Add this line to explicitly set the naming strategy
                //explicitlyEncodeElementWrapper = false // (Might be needed for certain structures)
                // Use the correct naming strategy for snake_case if your backend relies on default Jackson
                // You might need to add a dependency for this if not already there,
                // or just rely on the @SerializedName annotations.
                // The current setup with @SerializedName is generally clearer.
                // It might look like: namingStrategy = JsonNamingStrategy.SnakeCase
                // However, with @SerializedName, you don't need a global strategy.
            })
        }
        // You might add logging or other features here
    }

    override suspend fun login(request: LoginRequestDto): LoginResponseDto {
        val response = client.post("$BASE_URL/auth/login") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }

        if (response.status == HttpStatusCode.OK) {
            return response.body<LoginResponseDto>()
        } else {
            val errorBody = response.body<String>()
            throw Exception("Login failed with status ${response.status}: $errorBody")
        }
    }



}