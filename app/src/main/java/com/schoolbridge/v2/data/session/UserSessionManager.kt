package com.schoolbridge.v2.data.session

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import com.schoolbridge.v2.domain.user.Gender // Assuming this is defined elsewhere
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

// Top-level property for DataStore instance.
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

// --- CurrentUser Data Class (Your domain model for the logged-in user) ---
data class CurrentUser(
    val userId: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val activeRoles: List<String>,
    val phoneNumber: String?,
    val nationalId: String?,
    val address: Address?, // Nested data class
    val profilePictureUrl: String?,
    val role: String?, // Assuming a primary role for the user
    val joinDate: String?,
    val linkedStudents: List<LinkedStudent>? // Nested data class
) {
    @Serializable
    data class Address(
        val district: String?,
        val sector: String?,
        val cell: String?,
        val village: String?
    )

    @Serializable
    data class LinkedStudent(
        val id: String,
        val firstName: String,
        val lastName: String
    )
    val gender: Gender? = null // Example: You might need to parse this from LoginResponseDto
}

@Singleton
class UserSessionManager @Inject constructor(private val context: Context) {

    init {
        Log.d("UserSessionManager", "UserSessionManager constructor called and injected.")
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
        // val GENDER = stringPreferencesKey("userGender")
    }

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)

    // THIS IS THE CRUCIAL CHANGE: Expose as a ReadonlyStateFlow, NOT Compose's MutableState
    val currentUser: Flow<CurrentUser?> = _currentUser.asStateFlow()

    private val gson = Gson()

    val isLoggedIn: Flow<Boolean> = context.userDataStore.data
        .map { preferences ->
            val tokenValue = preferences[PreferencesKeys.AUTH_TOKEN]
            tokenValue != null
        }
        .catch { e ->
            Log.e("UserSessionManager", "Error collecting isLoggedIn Flow: ${e.message}", e)
            emit(false)
        }

    suspend fun initializeSession() {
        Log.d("UserSessionManager", "initializeSession() called.")
        try {
            val prefs = context.userDataStore.data.first()

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

            if (authToken != null && userId != null && email != null && firstName != null && lastName != null && activeRolesString != null) {
                val address = addressJson?.let {
                    if (it.isNotBlank()) gson.fromJson(it, CurrentUser.Address::class.java) else null
                }
                val linkedStudents = linkedStudentsJson?.let {
                    if (it.isNotBlank()) gson.fromJson<List<CurrentUser.LinkedStudent>>(it,
                        object : TypeToken<List<CurrentUser.LinkedStudent>>() {}.type
                    ) else null
                }

                _currentUser.value = CurrentUser(
                    userId = userId,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    activeRoles = activeRolesString.split(",").map { it.trim() },
                    phoneNumber = phoneNumber,
                    nationalId = nationalId,
                    address = address,
                    profilePictureUrl = profilePictureUrl,
                    role = primaryRole,
                    joinDate = joinDate,
                    linkedStudents = linkedStudents
                )
                Log.d("UserSessionManager", "Session successfully initialized for user: $userId")
            } else {
                _currentUser.value = null
                Log.d("UserSessionManager", "No complete active session found during initialization (some data was null).")
            }
        } catch (e: Exception) {
            Log.e("UserSessionManager", "Critical error during initializeSession: ${e.message}", e)
            _currentUser.value = null
        }
    }

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

                preferences[PreferencesKeys.PHONE_NUMBER] = response.phoneNumber ?: ""
                preferences[PreferencesKeys.NATIONAL_ID] = response.nationalId ?: ""
                preferences[PreferencesKeys.ADDRESS_JSON] = gson.toJson(response.address) ?: ""
                preferences[PreferencesKeys.PROFILE_PICTURE_URL] = response.profilePictureUrl ?: ""
                preferences[PreferencesKeys.PRIMARY_ROLE] = response.role ?: ""
                preferences[PreferencesKeys.JOIN_DATE] = response.joinDate ?: ""
                preferences[PreferencesKeys.LINKED_STUDENTS_JSON] = gson.toJson(response.linkedStudents) ?: ""

                Log.d("UserSessionManager", "DataStore edit block completed.")
            }

            // Update the StateFlow after saving to DataStore
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
                linkedStudents = response.linkedStudents
            )
            Log.d("UserSessionManager", "Login response saved and _currentUser updated for user: ${response.userId}")
        } catch (e: Exception) {
            Log.e("UserSessionManager", "Error saving login response: ${e.message}", e)
        }
    }

    open suspend fun clearSession() {
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