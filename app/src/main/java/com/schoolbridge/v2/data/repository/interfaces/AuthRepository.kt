package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.preferences.UserPreferencesDto

// This is an interface (a contract), not an implementation.
// The actual implementation (e.g., AuthRepositoryImpl) would connect to your identity provider or user database.

/**
 * Interface for the **Authentication and User Management Repository**.
 *
 * This repository defines the contract for handling user authentication (login, logout, registration),
 * authorization (roles, permissions), and core user profile management.
 *
 * **Typical methods it would expose:**
 * -   User login and session management.
 * -   User registration.
 * -   Retrieving and updating user profiles.
 * -   Managing user roles and permissions.
 * -   Password management (e.g., reset, change).
 */
interface AuthRepository {

    suspend fun login(request: Any): Any // Replace Any with your UserLoginRequestDto and AuthTokenDto
    suspend fun logout(userId: String): Boolean
    suspend fun registerUser(user: Any): Any // Replace Any with your UserDto
    suspend fun getUserById(userId: String): Any? // Replace Any? with your UserDto
    suspend fun updateUserProfile(user: Any): Any // Replace Any with your UserDto
    suspend fun changePassword(userId: String, oldPass: String, newPass: String): Boolean // Example
    suspend fun resetPassword(email: String): Boolean // Example for forgotten password
    suspend fun getUserPreferences(userId: String): UserPreferencesDto?
    suspend fun updateUserPreferences(preferences: UserPreferencesDto): UserPreferencesDto
    suspend fun verifyUserIdentity(userId: String, verificationDetails: Any): Any // Replace Any with specific DTOs
}