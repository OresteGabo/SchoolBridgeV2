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
import kotlinx.coroutines.launch
import kotlinx.serialization.SerializationException
import java.io.IOException

class LoginViewModel(
    private val authApiService: AuthApiService // Inject the API service
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
        loginError = null // Clear error when user types
    }

    fun onPasswordChange(newValue: String) {
        password = newValue
        loginError = null // Clear error when user types
    }

    fun login() {
        // Basic validation
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            loginError = "Username/Email and Password cannot be empty."
            return
        }

        isLoading = true
        loginError = null // Clear previous errors
        loginSuccess = null // Clear previous success

        viewModelScope.launch {
            try {
                val request = LoginRequestDto(usernameOrEmail, password)
                Log.d("ERROR_LOGIN", "Sending login request: $request")
                val response = authApiService.login(request)
                Log.d("ERROR_LOGIN_RESPONSE", "Login response: $response")
                loginSuccess = response
            } catch (e: SerializationException) {
                Log.e("ERROR_LOGIN", "Serialization error: ${e.message}")
                loginError = "Serialization error occurred."
            } catch (e: IOException) {
                Log.e("ERROR_LOGIN", "Network error: ${e.message}")
                loginError = "Network error occurred."
            } catch (e: Exception) {
                Log.e("ERROR_LOGIN", "Unexpected error: ${e.message}")
                loginError = "An unexpected error occurred."
            } finally {
                isLoading = false
            }

        }
    }

    // Optional: Clear state for next login attempt if needed
    fun resetState() {
        usernameOrEmail = ""
        password = ""
        isLoading = false
        loginError = null
        loginSuccess = null
    }
}