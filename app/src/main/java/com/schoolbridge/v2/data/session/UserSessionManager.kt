package com.schoolbridge.v2.data.session

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import com.schoolbridge.v2.domain.user.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/* ────────────────────────────────────────────────────────────── */
/* DataStore instance (extension on Context)                      */
/* ────────────────────────────────────────────────────────────── */
val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/* ────────────────────────────────────────────────────────────── */
/* UserSessionManager                                            */
/* ────────────────────────────────────────────────────────────── */
@Singleton
class UserSessionManager @Inject constructor(
    private val context: Context,
    private val gson: Gson = Gson()
) {
    /* Keys */
    private object Keys {
        val AUTH_TOKEN          = stringPreferencesKey("authToken")
        val REFRESH_TOKEN       = stringPreferencesKey("refreshToken")
        val USER_ID             = stringPreferencesKey("userId")
        val EMAIL               = stringPreferencesKey("userEmail")
        val FIRST_NAME          = stringPreferencesKey("userFirstName")
        val LAST_NAME           = stringPreferencesKey("userLastName")
        val ACTIVE_ROLES        = stringPreferencesKey("userActiveRoles")    // CSV of enum names
        val CURRENT_ROLE        = stringPreferencesKey("userCurrentRole")    // Single enum name

        val PHONE_NUMBER        = stringPreferencesKey("userPhoneNumber")
        val NATIONAL_ID         = stringPreferencesKey("userNationalId")
        val ADDRESS_JSON        = stringPreferencesKey("userAddressJson")
        val PROFILE_PICTURE_URL = stringPreferencesKey("userProfilePictureUrl")
        val JOIN_DATE           = stringPreferencesKey("userJoinDate")
        val LINKED_STUDENTS_JSON= stringPreferencesKey("userLinkedStudentsJson")
        val GENDER              = stringPreferencesKey("userGender")
        val IS_VERIFIED         = booleanPreferencesKey("isVerified")
    }

    /* In-memory state */
    private val _currentUser = MutableStateFlow<CurrentUser?>(null)
    val currentUser: StateFlow<CurrentUser?> = _currentUser.asStateFlow()

    /* Convenience flows */
    val isLoggedIn: Flow<Boolean> = context.userDataStore.data
        .map { prefs -> prefs[Keys.AUTH_TOKEN] != null && prefs[Keys.USER_ID] != null }
        .catch { emit(false) }

    val userId: Flow<String?>      = currentUser.map { it?.userId }
    val currentRole: Flow<UserRole?> = currentUser.map { it?.currentRole }

    /* ────────────────────────────────────────────────────────── */
    /* Session restoration                                       */
    /* ────────────────────────────────────────────────────────── */
    suspend fun initializeSession() {
        Log.d("UserSessionManager", "initializeSession()")
        runCatching { context.userDataStore.data.first() }
            .onSuccess { prefs ->
                _currentUser.value = prefs.toCurrentUserOrNull()
                Log.d("UserSessionManager", "Initialized: user=${_currentUser.value?.userId}")
            }
            .onFailure {
                Log.e("UserSessionManager", "init failed", it)
                _currentUser.value = null
            }
    }

    /* ────────────────────────────────────────────────────────── */
    /* Save login response (now returns CurrentUser)             */
    /* ────────────────────────────────────────────────────────── */
    suspend fun saveLoginResponse(dto: LoginResponseDto): CurrentUser {
        Log.d("UserSessionManager", "saveLoginResponse(${dto.userId})")

        val rolesCsv   = dto.activeRoles.joinToString(",")         // assume dto.activeRoles is List<String>
        val roleString = dto.role ?: dto.activeRoles.firstOrNull() // pick default if server sends none

        /* Persist everything */
        context.userDataStore.edit { prefs ->
            prefs[Keys.AUTH_TOKEN]           = dto.authToken
            prefs[Keys.REFRESH_TOKEN]        = dto.refreshToken
            prefs[Keys.USER_ID]              = dto.userId
            prefs[Keys.EMAIL]                = dto.email
            prefs[Keys.FIRST_NAME]           = dto.firstName
            prefs[Keys.LAST_NAME]            = dto.lastName
            prefs[Keys.ACTIVE_ROLES]         = rolesCsv
            prefs[Keys.CURRENT_ROLE]         = roleString ?: ""

            prefs[Keys.PHONE_NUMBER]         = dto.phoneNumber ?: ""
            prefs[Keys.NATIONAL_ID]          = dto.nationalId ?: ""
            prefs[Keys.ADDRESS_JSON]         = gson.toJson(dto.address) ?: ""
            prefs[Keys.PROFILE_PICTURE_URL]  = dto.profilePictureUrl ?: ""
            prefs[Keys.JOIN_DATE]            = dto.joinDate ?: ""
            prefs[Keys.LINKED_STUDENTS_JSON] = gson.toJson(dto.linkedStudents) ?: ""
            prefs[Keys.GENDER]               = dto.gender?.name ?: ""
            prefs[Keys.IS_VERIFIED]          = dto.isVerified
        }

        /* Build user & push into flow immediately */
        val user = dto.toCurrentUser()
        _currentUser.value = user
        return user
    }

    /* ────────────────────────────────────────────────────────── */
    /* Changing ONLY the current role                            */
    /* ────────────────────────────────────────────────────────── */
    suspend fun setCurrentRole(role: UserRole) {
        context.userDataStore.edit { prefs ->
            prefs[Keys.CURRENT_ROLE] = role.name
        }
        _currentUser.value = _currentUser.value?.copy(currentRole = role)
        Log.d("UserSessionManager", "Current role set to ${role.name}")
    }

    /* ────────────────────────────────────────────────────────── */
    /* Other helpers                                             */
    /* ────────────────────────────────────────────────────────── */
    suspend fun clearSession() {
        context.userDataStore.edit { it.clear() }
        _currentUser.value = null
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

    /* ────────────────────────────────────────────────────────── */
    /* Mapping helpers                                           */
    /* ────────────────────────────────────────────────────────── */
    private fun LoginResponseDto.toCurrentUser() = CurrentUser(
        userId             = userId,
        email              = email,
        firstName          = firstName,
        lastName           = lastName,
        activeRoles        = activeRoles.mapNotNull { it.toUserRoleOrNull() }.toSet(),
        phoneNumber        = phoneNumber,
        nationalId         = nationalId,
        address            = address,
        profilePictureUrl  = profilePictureUrl,
        currentRole        = (role ?: activeRoles.firstOrNull())?.toUserRoleOrNull(),
        joinDate           = joinDate,
        linkedStudents     = linkedStudents,
        gender             = gender,
        isVerified         = isVerified
    )

    private fun Preferences.toCurrentUserOrNull(): CurrentUser? {
        val authToken = this[Keys.AUTH_TOKEN] ?: return null
        val userId    = this[Keys.USER_ID]   ?: return null
        val email     = this[Keys.EMAIL]     ?: return null
        val firstName = this[Keys.FIRST_NAME]?: return null
        val lastName  = this[Keys.LAST_NAME] ?: return null

        val rolesCsv      = this[Keys.ACTIVE_ROLES] ?: ""
        val currentRoleStr= this[Keys.CURRENT_ROLE]

        val addressJson   = this[Keys.ADDRESS_JSON]
        val linkedJson    = this[Keys.LINKED_STUDENTS_JSON]
        val genderStr     = this[Keys.GENDER]

        val address = addressJson?.takeIf { it.isNotBlank() }?.let {
            gson.fromJson(it, CurrentUser.Address::class.java)
        }
        val linkedStudents = linkedJson?.takeIf { it.isNotBlank() }?.let {
            gson.fromJson<List<CurrentUser.LinkedStudent>>(
                it, object : TypeToken<List<CurrentUser.LinkedStudent>>() {}.type
            )
        }
        val gender = genderStr?.runCatching { Gender.valueOf(this) }?.getOrNull()

        return CurrentUser(
            userId             = userId,
            email              = email,
            firstName          = firstName,
            lastName           = lastName,
            activeRoles        = rolesCsv.toRoleSet(),
            phoneNumber        = this[Keys.PHONE_NUMBER],
            nationalId         = this[Keys.NATIONAL_ID],
            address            = address,
            profilePictureUrl  = this[Keys.PROFILE_PICTURE_URL],
            currentRole        = currentRoleStr?.toUserRoleOrNull(),
            joinDate           = this[Keys.JOIN_DATE],
            linkedStudents     = linkedStudents,
            gender             = gender,
            isVerified         = this[Keys.IS_VERIFIED] ?: false
        ).also {
            Log.d("UserSessionManager", "Prefs→User mapped (${it.userId})")
        }
    }

    /* ────────────────────────────────────────────────────────── */
    /* Small extension helpers                                   */
    /* ────────────────────────────────────────────────────────── */
    private fun String.toUserRoleOrNull(): UserRole? =
        runCatching { UserRole.valueOf(this) }.getOrNull()

    private fun String.toRoleSet(): Set<UserRole> =
        split(',').mapNotNull { it.trim().takeIf { it.isNotEmpty() }?.toUserRoleOrNull() }.toSet()
}

