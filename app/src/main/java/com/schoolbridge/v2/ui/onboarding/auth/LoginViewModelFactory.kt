// src/main/java/com/schoolbridge/v2/ui/onboarding/auth/LoginViewModelFactory.kt
package com.schoolbridge.v2.ui.onboarding.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.schoolbridge.v2.data.remote.AuthApiService
import com.schoolbridge.v2.data.session.UserSessionManager // Import UserSessionManager

class LoginViewModelFactory(
    private val authApiService: AuthApiService,
    private val userSessionManager: UserSessionManager // Add UserSessionManager
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authApiService, userSessionManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}