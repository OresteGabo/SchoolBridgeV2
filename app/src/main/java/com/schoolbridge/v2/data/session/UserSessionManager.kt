package com.schoolbridge.v2.data.session

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey // NEW IMPORT
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.domain.user.Gender // Assuming this is defined elsewhere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Top-level property for DataStore instance.
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserSessionManager @Inject constructor(private val context: Context) {

    init {
        Log.d("UserSessionManager", "UserSessionManager constructor called and injected.")
        // It's good practice to initialize the session here
        // However, be careful not to block the main thread.
        // It's better to call this from a lifecycle-aware scope, e.g., in App's Application class
        // or ensure it's called from a background coroutine when the app starts.
        // For now, we'll keep it here assuming a suitable setup for init.
    }

    private object PreferencesKeys {
        val AUTH_TOKEN = stringPreferencesKey("authToken")
        val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
        val USER_ID = stringPreferencesKey("userId")
        val EMAIL = stringPreferencesKey("userEmail")
        val FIRST_NAME = stringPreferencesKey("userFirstName")
        val LAST_NAME = stringPreferencesKey("userLastName")
        val ACTIVE_ROLES = stringPreferencesKey("userActiveRoles")

        val PHONE_NUMBER = stringPreferencesKey("userPhoneNumber")
        val NATIONAL_ID = stringPreferencesKey("userNationalId")
        val ADDRESS_JSON = stringPreferencesKey("userAddressJson")
        val PROFILE_PICTURE_URL = stringPreferencesKey("userProfilePictureUrl")
        val PRIMARY_ROLE = stringPreferencesKey("userPrimaryRole")
        val JOIN_DATE = stringPreferencesKey("userJoinDate")
        val LINKED_STUDENTS_JSON = stringPreferencesKey("userLinkedStudentsJson")
        val GENDER = stringPreferencesKey("userGender") // <--- ADDED THIS KEY
        val IS_VERIFIED = booleanPreferencesKey("isVerified") // <--- ADDED THIS KEY
    }

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)

    // THIS IS THE CRUCIAL CHANGE: Expose as a ReadonlyStateFlow, NOT Compose's MutableState
    val currentUser: Flow<CurrentUser?> = _currentUser.asStateFlow() // Use Flow for observing changes

    private val gson = Gson()

    val isLoggedIn: Flow<Boolean> = context.userDataStore.data
        .map { preferences ->
            val tokenValue = preferences[PreferencesKeys.AUTH_TOKEN]
            tokenValue != null
        }
        .catch { e ->
            Log.e("UserSessionManager", "Error collecting isLoggedIn Flow: ${e.message}", e)
            emit(false) // Emit false on error
        }

    /**
     * Initializes the user session by loading data from DataStore.
     * This should be called once when the application starts to restore the session.
     */
    suspend fun initializeSession() {
        Log.d("UserSessionManager", "initializeSession() called.")
        try {
            val prefs = context.userDataStore.data.first() // Get current preferences snapshot

            val authToken = prefs[PreferencesKeys.AUTH_TOKEN]
            val userId = prefs[PreferencesKeys.USER_ID]
            val email = prefs[PreferencesKeys.EMAIL]
            val firstName = prefs[PreferencesKeys.FIRST_NAME]
            val lastName = prefs[PreferencesKeys.LAST_NAME]
            val activeRolesString = prefs[PreferencesKeys.ACTIVE_ROLES]
            val phoneNumber = prefs[PreferencesKeys.PHONE_NUMBER]
            val nationalId = prefs[PreferencesKeys.NATIONAL_ID]
            val addressJson = prefs[PreferencesKeys.ADDRESS_JSON]
            val profilePictureUrl = prefs[PreferencesKeys.PROFILE_PICTURE_URL]
            val primaryRole = prefs[PreferencesKeys.PRIMARY_ROLE]
            val joinDate = prefs[PreferencesKeys.JOIN_DATE]
            val linkedStudentsJson = prefs[PreferencesKeys.LINKED_STUDENTS_JSON]
            val genderString = prefs[PreferencesKeys.GENDER] // NEW
            val isVerified = prefs[PreferencesKeys.IS_VERIFIED] // NEW

            if (authToken != null && userId != null && email != null && firstName != null && lastName != null && activeRolesString != null) {
                val address = addressJson?.let {
                    if (it.isNotBlank()) gson.fromJson(it, CurrentUser.Address::class.java) else null
                }
                val linkedStudents = linkedStudentsJson?.let {
                    if (it.isNotBlank()) gson.fromJson<List<CurrentUser.LinkedStudent>>(it,
                        object : TypeToken<List<CurrentUser.LinkedStudent>>() {}.type
                    ) else null
                }
                val gender = try {
                    genderString?.let { Gender.valueOf(it) }
                } catch (e: IllegalArgumentException) {
                    Log.e("UserSessionManager", "Invalid gender string: $genderString", e)
                    null
                }

                _currentUser.value = CurrentUser(
                    userId = userId,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    activeRoles = activeRolesString.split(",").map { it.trim() }.filter { it.isNotBlank() }, // Ensure empty strings are not added
                    phoneNumber = phoneNumber,
                    nationalId = nationalId,
                    address = address,
                    profilePictureUrl = profilePictureUrl,
                    role = primaryRole,
                    joinDate = joinDate,
                    linkedStudents = linkedStudents,
                    gender = gender, // NEW
                    isVerified = isVerified ?: false // NEW: Default to false if not found
                )
                Log.d("UserSessionManager", "Session successfully initialized for user: $userId (Verified: ${isVerified ?: false})")
            } else {
                _currentUser.value = null
                Log.d("UserSessionManager", "No complete active session found during initialization (some data was null).")
            }
        } catch (e: Exception) {
            Log.e("UserSessionManager", "Critical error during initializeSession: ${e.message}", e)
            _currentUser.value = null
        }
    }

    /**
     * Saves the login response to DataStore and updates the current user in session.
     * This function is suspend, so it will block until DataStore operations are complete.
     */
    suspend fun saveLoginResponse(response: LoginResponseDto) {
        Log.d("UserSessionManager", "saveLoginResponse() called for user: ${response.userId}")
        try {
            context.userDataStore.edit { preferences ->
                Log.d("UserSessionManager", "Saving login response to DataStore...")
                preferences[PreferencesKeys.AUTH_TOKEN] = response.authToken
                preferences[PreferencesKeys.REFRESH_TOKEN] = response.refreshToken
                preferences[PreferencesKeys.USER_ID] = response.userId
                preferences[PreferencesKeys.EMAIL] = response.email
                preferences[PreferencesKeys.FIRST_NAME] = response.firstName
                preferences[PreferencesKeys.LAST_NAME] = response.lastName
                preferences[PreferencesKeys.ACTIVE_ROLES] = response.activeRoles.joinToString(",")

                // Save nullable fields, providing default empty string/false if null
                preferences[PreferencesKeys.PHONE_NUMBER] = response.phoneNumber ?: ""
                preferences[PreferencesKeys.NATIONAL_ID] = response.nationalId ?: ""
                preferences[PreferencesKeys.ADDRESS_JSON] = gson.toJson(response.address) ?: ""
                preferences[PreferencesKeys.PROFILE_PICTURE_URL] = response.profilePictureUrl ?: ""
                preferences[PreferencesKeys.PRIMARY_ROLE] = response.role ?: ""
                preferences[PreferencesKeys.JOIN_DATE] = response.joinDate ?: ""
                preferences[PreferencesKeys.LINKED_STUDENTS_JSON] = gson.toJson(response.linkedStudents) ?: ""
                preferences[PreferencesKeys.GENDER] = response.gender?.name ?: "" // NEW: Save Gender as string
                preferences[PreferencesKeys.IS_VERIFIED] = response.isVerified // NEW: Save isVerified boolean

                Log.d("UserSessionManager", "DataStore edit block completed.")
            }

            // After DataStore operations are complete, update the in-memory StateFlow
            // This ensures that `_currentUser.value` reflects the latest saved data
            _currentUser.value = CurrentUser(
                userId = response.userId,
                email = response.email,
                firstName = response.firstName,
                lastName = response.lastName,
                activeRoles = response.activeRoles,
                phoneNumber = response.phoneNumber,
                nationalId = response.nationalId,
                address = response.address,
                profilePictureUrl = response.profilePictureUrl,
                role = response.role,
                joinDate = response.joinDate,
                linkedStudents = response.linkedStudents,
                gender = response.gender, // NEW
                isVerified = response.isVerified // NEW
            )
            Log.d("UserSessionManager", "Login response saved and _currentUser updated for user: ${response.userId} (Verified: ${response.isVerified})")
        } catch (e: Exception) {
            Log.e("UserSessionManager", "Error saving login response: ${e.message}", e)
            // Consider what to do here: maybe reset session or set _currentUser to null
            // depending on desired error handling
        }
    }

    /**
     * Clears the user session data from DataStore and resets the in-memory user.
     */
    suspend fun clearSession() {
        Log.d("UserSessionManager", "clearSession() called.")
        try {
            context.userDataStore.edit { preferences ->
                preferences.clear()
                Log.d("UserSessionManager", "DataStore preferences cleared.")
            }
            _currentUser.value = null
            Log.d("UserSessionManager", "Session cleared and _currentUser set to null.")
        } catch (e: Exception) {
            Log.e("UserSessionManager", "Error clearing session: ${e.message}", e)
        }
    }

    /**
     * Retrieves the authentication token from DataStore.
     */
    suspend fun getAuthToken(): String? {
        Log.d("UserSessionManager", "getAuthToken() called.")
        return try {
            val token = context.userDataStore.data.first()[PreferencesKeys.AUTH_TOKEN]
            Log.d("UserSessionManager", "Auth token retrieved: ${token?.take(5)}... (exists: ${token != null})")
            token
        } catch (e: Exception) {
            Log.e("UserSessionManager", "Error getting auth token: ${e.message}", e)
            null
        }
    }
}