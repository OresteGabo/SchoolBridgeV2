package com.schoolbridge.v2.data.remote

import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.io.IOException

class UserReadableHttpException(
    override val message: String,
    val statusCode: Int? = null,
    cause: Throwable? = null
) : IOException(message, cause)

private val httpErrorJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
}

suspend fun <T> runApiCall(
    defaultMessage: String,
    authFailureStatusCodes: Set<Int> = emptySet(),
    onAuthFailure: (suspend (UserReadableHttpException) -> Unit)? = null,
    block: suspend () -> T
): T {
    return try {
        block()
    } catch (throwable: Throwable) {
        val mappedError = throwable.toUserReadableHttpException(defaultMessage)
        if (mappedError.statusCode in authFailureStatusCodes) {
            onAuthFailure?.invoke(mappedError)
        }
        throw mappedError
    }
}

suspend fun Throwable.toUserReadableHttpException(
    defaultMessage: String
): UserReadableHttpException {
    if (this is UserReadableHttpException) return this

    return when (this) {
        is ResponseException -> {
            val body = runCatching { response.bodyAsText() }.getOrNull()
            fromStatus(
                statusCode = response.status.value,
                responseBody = body,
                defaultMessage = defaultMessage,
                cause = this
            )
        }

        is HttpRequestTimeoutException -> UserReadableHttpException(
            message = "The request took too long. Please try again.",
            cause = this
        )

        is SerializationException -> UserReadableHttpException(
            message = "The server returned an unexpected response. Please try again.",
            cause = this
        )

        is IllegalStateException -> {
            if (message?.contains("Missing auth token", ignoreCase = true) == true) {
                UserReadableHttpException(
                    message = "Your session has expired. Please sign in again.",
                    statusCode = 401,
                    cause = this
                )
            } else {
                UserReadableHttpException(
                    message = defaultMessage,
                    cause = this
                )
            }
        }

        is IOException -> UserReadableHttpException(
            message = "Unable to reach the server. Please check your internet connection.",
            cause = this
        )

        else -> UserReadableHttpException(
            message = message?.takeIf { it.isNotBlank() } ?: defaultMessage,
            cause = this
        )
    }
}

fun fromStatus(
    statusCode: Int,
    responseBody: String?,
    defaultMessage: String,
    cause: Throwable? = null
): UserReadableHttpException {
    val serverMessage = extractServerMessage(responseBody)
    val message = when (statusCode) {
        400 -> serverMessage ?: "The request could not be understood. Please review your input."
        401 -> serverMessage ?: "Your session has expired. Please sign in again."
        403 -> serverMessage ?: "You do not have permission to do that."
        404 -> serverMessage ?: "We could not find what you requested."
        408 -> serverMessage ?: "The request timed out. Please try again."
        409 -> serverMessage ?: "This action conflicts with existing data."
        422 -> serverMessage ?: "Some information is missing or invalid."
        429 -> serverMessage ?: "Too many requests right now. Please wait a moment and try again."
        in 500..599 -> serverMessage ?: "The server is having a problem right now. Please try again soon."
        else -> serverMessage ?: defaultMessage
    }

    return UserReadableHttpException(
        message = message,
        statusCode = statusCode,
        cause = cause
    )
}

private fun extractServerMessage(responseBody: String?): String? {
    if (responseBody.isNullOrBlank()) return null

    return runCatching {
        val jsonObject = httpErrorJson.parseToJsonElement(responseBody).jsonObject
        listOf("message", "error", "detail")
            .firstNotNullOfOrNull { key -> jsonObject[key]?.jsonPrimitive?.contentOrNull }
            ?.takeIf { it.isNotBlank() }
            ?.takeUnless(::isTooTechnicalServerMessage)
    }.getOrNull()
}

private fun isTooTechnicalServerMessage(message: String): Boolean {
    val normalized = message.trim().lowercase()
    return normalized in setOf(
        "forbidden",
        "unauthorized",
        "bad request",
        "not found",
        "internal server error",
        "service unavailable",
        "method not allowed",
        "request timeout"
    )
}
