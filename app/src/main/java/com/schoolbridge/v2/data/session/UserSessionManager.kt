package com.schoolbridge.v2.data.session

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import com.schoolbridge.v2.domain.user.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

@Singleton
class UserSessionManager @Inject constructor(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    val appContext: Context
        get() = context

    private object Keys {
        val AUTH_TOKEN = stringPreferencesKey("authToken")
        val REFRESH_TOKEN = stringPreferencesKey("refreshToken")
        val USER_ID = stringPreferencesKey("userId")
        val EMAIL = stringPreferencesKey("userEmail")
        val FIRST_NAME = stringPreferencesKey("userFirstName")
        val LAST_NAME = stringPreferencesKey("userLastName")
        val ACTIVE_ROLES = stringPreferencesKey("userActiveRoles")
        val CURRENT_ROLE = stringPreferencesKey("userCurrentRole")
        val PHONE_NUMBER = stringPreferencesKey("userPhoneNumber")
        val NATIONAL_ID = stringPreferencesKey("userNationalId")
        val ADDRESS_JSON = stringPreferencesKey("userAddressJson")
        val PROFILE_PICTURE_URL = stringPreferencesKey("userProfilePictureUrl")
        val JOIN_DATE = stringPreferencesKey("userJoinDate")
        val LINKED_STUDENTS_JSON = stringPreferencesKey("userLinkedStudentsJson")
        val GENDER = stringPreferencesKey("userGender")
        val IS_VERIFIED = booleanPreferencesKey("isVerified")
    }

    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    private val _sessionState = MutableStateFlow<SessionState>(SessionState.Loading)
    val sessionState: StateFlow<SessionState> = _sessionState.asStateFlow()

    val isLoggedIn: Flow<Boolean> = sessionState.map { it is SessionState.LoggedIn }
    val userId: Flow<String?> = currentUser.map { it?.userId }
    val currentRole: Flow<UserRole?> = currentUser.map { it?.currentRole }

    fun getAuthTokenSync(): String? = runBlocking {
        context.userDataStore.data.first()[Keys.AUTH_TOKEN]
    }

    fun getCurrentUserIdSync(): String? = runBlocking {
        context.userDataStore.data.first()[Keys.USER_ID]
    }
    suspend fun initializeSession() {
        Log.d("UserSessionManager", "initializeSession()")

        val isFirstTime = isFirstLaunch()
        _sessionState.emit(
            when {
                isFirstTime -> SessionState.Onboarding
                else -> SessionState.LoggedOut
            }
        )

        runCatching { context.userDataStore.data.first() }
            .onSuccess { prefs ->
                val user = prefs.toCurrentUserOrNull()
                _currentUser.value = user
                _sessionState.value = if (user != null) SessionState.LoggedIn(user)
                else SessionState.LoggedOut
                Log.d("UserSessionManager", "Init complete: ${_sessionState.value}")
            }
            .onFailure {
                _currentUser.value = null
                _sessionState.value = SessionState.LoggedOut
                Log.e("UserSessionManager", "Init failed", it)
            }
    }

    suspend fun saveLoginResponse(dto: LoginResponseDto): CurrentUser {
        Log.d("UserSessionManager", "saveLoginResponse(${dto.user.id})")
        Log.d("UserSessionManager", "Saving auth token")

        val rolesCsv = dto.user.roles.joinToString(",")
        val roleString = dto.user.roles.firstOrNull()

        context.userDataStore.edit { prefs ->
            prefs[Keys.AUTH_TOKEN] = dto.token
            prefs[Keys.REFRESH_TOKEN] = ""
            prefs[Keys.USER_ID] = dto.user.id.toString()
            prefs[Keys.EMAIL] = dto.user.email
            prefs[Keys.FIRST_NAME] = dto.user.firstName
            prefs[Keys.LAST_NAME] = dto.user.familyName
            prefs[Keys.ACTIVE_ROLES] = rolesCsv
            prefs[Keys.CURRENT_ROLE] = roleString ?: ""
            prefs[Keys.PHONE_NUMBER] = ""
            prefs[Keys.NATIONAL_ID] = ""
            prefs[Keys.ADDRESS_JSON] = ""
            prefs[Keys.PROFILE_PICTURE_URL] = ""
            prefs[Keys.JOIN_DATE] = dto.user.lastLogin ?: ""
            prefs[Keys.LINKED_STUDENTS_JSON] = gson.toJson(emptyList<CurrentUser.LinkedStudent>())
            prefs[Keys.GENDER] = ""
            prefs[Keys.IS_VERIFIED] = dto.user.isActive
        }

        // Read back saved values to confirm
        val savedPrefs = context.userDataStore.data.first()
        Log.d("UserSessionManager", "Retrieved auth token: ${savedPrefs[Keys.AUTH_TOKEN] != null}")
        Log.d("UserSessionManager", "Retrieved user ID: ${savedPrefs[Keys.USER_ID]}")
        Log.d("UserSessionManager", "Retrieved email: ${savedPrefs[Keys.EMAIL]}")

        val user = CurrentUser(
            userId = dto.user.id.toString(),
            email = dto.user.email,
            firstName = dto.user.firstName,
            lastName = dto.user.familyName,
            activeRoles = dto.user.roles.mapNotNull { it.toUserRoleOrNull() }.toSet(),
            phoneNumber = null,
            nationalId = null,
            address = null,
            profilePictureUrl = null,
            currentRole = dto.user.roles.firstOrNull()?.toUserRoleOrNull(),
            joinDate = dto.user.lastLogin,
            linkedStudents = emptyList(),
            gender = null,
            isVerified = dto.user.isActive
        )

        _currentUser.value = user
        _sessionState.value = SessionState.LoggedIn(user)

        Log.d("UserSessionManager", "User session initialized for: ${user.firstName}")
        return user
    }



    suspend fun setCurrentRole(role: UserRole) {
        context.userDataStore.edit { prefs ->
            prefs[Keys.CURRENT_ROLE] = role.name
        }
        _currentUser.value = _currentUser.value?.copy(currentRole = role)
        Log.d("UserSessionManager", "Current role set to ${role.name}")
    }

    suspend fun clearSession() {
        context.userDataStore.edit { it.clear() }
        _currentUser.value = null
        _sessionState.value = SessionState.LoggedOut
        Log.d("UserSessionManager", "Session cleared")
    }

    suspend fun refreshCurrentUser() = initializeSession()

    suspend fun updateLinkedStudents(newList: List<CurrentUser.LinkedStudent>) {
        context.userDataStore.edit { prefs ->
            prefs[Keys.LINKED_STUDENTS_JSON] = gson.toJson(newList)
        }
        _currentUser.value = _currentUser.value?.copy(linkedStudents = newList)
    }

    suspend fun getAuthToken(): String? =
        runCatching { context.userDataStore.data.first()[Keys.AUTH_TOKEN] }.getOrNull()

    suspend fun isFirstLaunch(): Boolean {
        val prefs = context.getSharedPreferences("prefs", MODE_PRIVATE)
        return prefs.getBoolean("first_launch", true)
    }

    suspend fun markOnboardingComplete() {
        val prefs = context.getSharedPreferences("prefs", MODE_PRIVATE)
        prefs.edit().putBoolean("first_launch", false).apply()
    }

    private fun Preferences.toCurrentUserOrNull(): CurrentUser? {
        val authToken = this[Keys.AUTH_TOKEN] ?: return null
        val userId = this[Keys.USER_ID] ?: return null
        val email = this[Keys.EMAIL] ?: return null
        val firstName = this[Keys.FIRST_NAME] ?: return null
        val lastName = this[Keys.LAST_NAME] ?: return null
        val rolesCsv = this[Keys.ACTIVE_ROLES] ?: return null
        val currentRoleStr = this[Keys.CURRENT_ROLE]
        val addressJson = this[Keys.ADDRESS_JSON]
        val linkedJson = this[Keys.LINKED_STUDENTS_JSON]
        val genderStr = this[Keys.GENDER]

        // Log the JSON strings to check their format
        Log.d("UserSessionManager", "Address JSON: $addressJson")
        Log.d("UserSessionManager", "Linked Students JSON: $linkedJson")

        val address = addressJson?.takeIf { it.isNotBlank() }?.let {
            try {
                gson.fromJson(it, CurrentUser.Address::class.java)
            } catch (e: JsonSyntaxException) {
                Log.e("UserSessionManager", "Failed to parse address JSON", e)
                null
            }
        }

        val linkedStudents = linkedJson?.takeIf { it.isNotBlank() }?.let {
            try {
                gson.fromJson<List<CurrentUser.LinkedStudent>>(
                    it,
                    object : TypeToken<List<CurrentUser.LinkedStudent>>() {}.type
                )
            } catch (e: JsonSyntaxException) {
                Log.e("UserSessionManager", "Failed to parse linked students JSON", e)
                null
            }
        }

        val gender = genderStr?.runCatching { Gender.valueOf(this) }?.getOrNull()

        return if (linkedStudents != null) {
            CurrentUser(
                userId = userId,
                email = email,
                firstName = firstName,
                lastName = lastName,
                activeRoles = rolesCsv.toRoleSet(),
                phoneNumber = this[Keys.PHONE_NUMBER],
                nationalId = this[Keys.NATIONAL_ID],
                address = address,
                profilePictureUrl = this[Keys.PROFILE_PICTURE_URL],
                currentRole = currentRoleStr?.toUserRoleOrNull(),
                joinDate = this[Keys.JOIN_DATE],
                linkedStudents = linkedStudents,
                gender = gender,
                isVerified = this[Keys.IS_VERIFIED] ?: false
            ).also {
                Log.d("UserSessionManager", "Prefs→User mapped (${it.userId})")
            }
        } else {
            null
        }
    }


    private fun String.toUserRoleOrNull(): UserRole? =
        runCatching { UserRole.valueOf(this) }.getOrNull()

    private fun String.toRoleSet(): Set<UserRole> =
        split(',').mapNotNull { it.trim().takeIf { it.isNotEmpty() }?.toUserRoleOrNull() }.toSet()

}
