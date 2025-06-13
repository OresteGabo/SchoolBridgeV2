// src/main/java/com/schoolbridge/v2/ui/onboarding/auth/LoginViewModel.kt
package com.schoolbridge.v2.ui.onboarding.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.auth.LoginRequestDto
import com.schoolbridge.v2.data.dto.auth.LoginResponseDto
import com.schoolbridge.v2.data.remote.AuthApiService
import com.schoolbridge.v2.data.session.UserSessionManager // Import your UserSessionManager
import kotlinx.coroutines.launch
import java.io.IOException
import kotlinx.serialization.SerializationException // Ensure this import is correct

class LoginViewModel(
    private val authApiService: AuthApiService,
    private val userSessionManager: UserSessionManager // Inject UserSessionManager
) : ViewModel() {

    var usernameOrEmail by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var loginError by mutableStateOf<String?>(null)
        private set

    var loginSuccess by mutableStateOf<LoginResponseDto?>(null)
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

                // Save the login response using the session manager
                userSessionManager.saveLoginResponse(response)

                loginSuccess = response // Update UI state
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
        // No need to clear username/password here if user might retry
        isLoading = false
        loginError = null
        loginSuccess = null
    }
}