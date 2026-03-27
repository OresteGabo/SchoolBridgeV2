package com.schoolbridge.v2.data.remote

import com.schoolbridge.v2.data.dto.user.RoleRequestDto
import com.schoolbridge.v2.data.session.UserSessionManager
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.accept
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.encodeToString
import io.ktor.http.content.TextContent

interface RoleRequestApiService {
    suspend fun submitRoleRequest(request: RoleRequestDto)
}

class RoleRequestApiServiceImpl(
    private val userSessionManager: UserSessionManager
) : RoleRequestApiService {

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

    override suspend fun submitRoleRequest(request: RoleRequestDto) {
        val token = userSessionManager.getAuthToken()
            ?: throw IllegalStateException("Missing auth token for role request")

        val payload = buildJsonObject {
            put("requestedRole", request.requestedRole)
            request.schoolId?.let { put("schoolId", it) }
            request.schoolName?.let { put("schoolName", it) }
            request.studentUserId?.let { put("studentUserId", it) }
            request.justification?.let { put("justification", it) }
            request.childStudentId?.let { put("childStudentId", it) }
            request.childNationalId?.let { put("childNationalId", it) }
            request.parentNationalId?.let { put("parentNationalId", it) }
            request.familyCardDocumentUrl?.let { put("familyCardDocumentUrl", it) }
            request.childName?.let { put("childName", it) }
            request.childDateOfBirth?.let { put("childDateOfBirth", it) }
            request.relationshipLabel?.let { put("relationshipLabel", it) }
            request.academicLevel?.let { put("academicLevel", it) }
            request.roleDescription?.let { put("roleDescription", it) }
            request.responsibilityScope?.let { put("responsibilityScope", it) }
            val docs = request.supportingDocumentsUrls.orEmpty()
            put("supportingDocumentsUrls", json.encodeToJsonElement(docs))
        }

        client.post("$BASE_URL/api/users/me/roles/request") {
            accept(ContentType.Application.Json)
            header(HttpHeaders.Authorization, "Bearer $token")
            setBody(TextContent(json.encodeToString(payload), ContentType.Application.Json))
        }
    }
}
