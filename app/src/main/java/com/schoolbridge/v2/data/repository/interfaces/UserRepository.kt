package com.schoolbridge.v2.data.repository.interfaces

import com.schoolbridge.v2.data.dto.user.UserDto
import com.schoolbridge.v2.data.preferences.UserPreferencesDto

// You would need to import your DTOs here, e.g.:
// import com.schoolvridge.v2.data.dto.UserDto
// import com.schoolvridge.v2.data.dto.UserPreferencesDto

/**
 * Interface for the **User Data Repository**.
 *
 * This repository defines the contract for accessing and managing user profiles
 * (beyond core authentication which might be handled by an AuthRepository) and user-specific preferences.
 * It's responsible for CRUD operations on user details.
 *
 * **Typical methods it would expose:**
 * -   Retrieving and updating user profiles (first name, last name, contact info, roles).
 * -   Managing user preferences.
 * -   (Optional) Basic user creation/deletion if not fully handled by AuthRepository.
 */
interface UserRepository {

    // User Profile Management
    suspend fun getUserById(userId: String): Any?
    suspend fun getUserByEmail(email: String): Any? // Useful for unique identifiers
    suspend fun createUser(user: UserDto): UserDto // If separate from AuthRepository registration
    suspend fun updateUserProfile(user: UserDto): UserDto
    suspend fun deleteUser(userId: String): Boolean

    // User Preferences Management
    suspend fun getUserPreferences(userId: String): UserPreferencesDto?
    suspend fun updateOrCreateUserPreferences(preferences: UserPreferencesDto): UserPreferencesDto

    suspend fun getUserRoles(userId: String): List<String>
}