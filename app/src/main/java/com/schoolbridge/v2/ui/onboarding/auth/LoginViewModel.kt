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
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
        loginSuccess = null

        viewModelScope.launch {
            try {
                val request = LoginRequestDto(usernameOrEmail, password)
                Log.d("LOGIN_FLOW", "Sending login request: $request")
                val response = authApiService.login(request)
                Log.d("LOGIN_FLOW", "Login response: $response")

                // ✅ Protect this from cancellation due to navigation
                val user = withContext(NonCancellable) {
                    userSessionManager.saveLoginResponse(response)
                }

                loginSuccess = user
                Log.d("LOGIN_FLOW", "Login complete. User: $user")

            } catch (e: SerializationException) {
                loginError = "Data format error. Please try again."
            } catch (e: IOException) {
                loginError = "Network error. Please check your internet connection."
            } catch (e: Exception) {
                Log.e("LOGIN_FLOW", "Unexpected error", e)
                loginError = "An unexpected error occurred: ${e.message}"
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