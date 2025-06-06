package com.schoolbridge.v2.data.dto.auth

import com.google.gson.annotations.SerializedName

/**
 * Data Transfer Object (DTO) for **requesting a new access token using a refresh token**.
 *
 * In token-based authentication systems (like OAuth 2.0 with JWTs), access tokens
 * are often short-lived for security. When an access token expires, this DTO is used
 * with a longer-lived refresh token to obtain a new access token without requiring
 * the user to re-enter their credentials. This process is typically handled silently
 * in the background by your authentication service.
 *
 * **Real-life Example:**
 * Your app's `AuthInterceptor` (in Retrofit) detects that the current access token
 * has expired. It then uses the stored `refreshToken` to send this DTO to a refresh endpoint
 * (e.g., `/auth/refresh-token`) to get a new `authToken` and `refreshToken` pair.
 *
 * @property refreshToken The long-lived refresh token previously obtained during login.
 * Example: `"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6y"`
 */
data class TokenRefreshRequestDto(
    @SerializedName("refresh_token") val refreshToken: String
)