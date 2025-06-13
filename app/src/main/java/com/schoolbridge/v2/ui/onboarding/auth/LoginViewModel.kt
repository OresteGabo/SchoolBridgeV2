package com.schoolbridge.v2.ui.onboarding.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.auth.LoginRequestDto
import com.schoolbridge.v2.data.remote.AuthApiService
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.user.CurrentUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.IOException
import kotlinx.serialization.SerializationException

class LoginViewModel(
    private val authApiService: AuthApiService,
    private val userSessionManager: UserSessionManager
) : ViewModel()
{

    var usernameOrEmail by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    val isSessionFullyReady = userSessionManager.currentUser
        .filterNotNull()
        .map { true }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // CHANGE 1: Change the type of loginSuccess to CurrentUser?
    var loginSuccess by mutableStateOf<CurrentUser?>(null)
        private set

    fun onUsernameOrEmailChange(newValue: String) {
        usernameOrEmail = newValue
        loginError = null
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        loginError = null
    }

    fun login() {
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            loginError = "Username/Email and Password cannot be empty."
            return
        }

        isLoading = true
        loginError = null
        loginSuccess = null // Reset success state

        viewModelScope.launch {
            try {
                val request = LoginRequestDto(usernameOrEmail, password)
                Log.d("LOGIN_FLOW", "Sending login request: $request")
                val response = authApiService.login(request)
                Log.d("LOGIN_FLOW", "Login response: $response")

                // Save the login response using the session manager.
                // This call is suspend and will complete *after* DataStore write and _currentUser update.
                userSessionManager.saveLoginResponse(response)
                Log.d("LOGIN_FLOW", "User session manager saveLoginResponse completed.")

                // CHANGE 2: Retrieve the CurrentUser directly from the session manager
                // This ensures we're signaling success *only* when the session manager has the data ready.
                // Using .first() on the StateFlow guarantees we wait for the first non-null value
                // which should be the one just set by saveLoginResponse.
                val currentUserAfterLogin = userSessionManager.currentUser.first { it != null }
                Log.d("LOGIN_FLOW", "Retrieved CurrentUser from session manager: $currentUserAfterLogin")

                // CHANGE 3: Set loginSuccess to the CurrentUser object
                loginSuccess = currentUserAfterLogin

            } catch (e: SerializationException) {
                Log.e("LOGIN_FLOW", "Serialization error: ${e.message}", e)
                loginError = "Data format error. Please try again."
            } catch (e: IOException) {
                Log.e("LOGIN_FLOW", "Network error: ${e.message}", e)
                loginError = "Network error occurred. Please check your internet connection."
            } catch (e: Exception) {
                Log.e("LOGIN_FLOW", "Unexpected error: ${e.message}", e)
                loginError = "An unexpected error occurred. Please try again later."
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        isLoading = false
        loginError = null
        loginSuccess = null
    }
}