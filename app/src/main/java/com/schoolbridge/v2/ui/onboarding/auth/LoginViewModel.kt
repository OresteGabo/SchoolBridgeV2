package com.schoolbridge.v2.ui.onboarding.auth

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.data.dto.auth.LoginRequestDto
import com.schoolbridge.v2.data.remote.AuthApiService
import com.schoolbridge.v2.data.remote.LoginResult
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

private const val AUTH_TRACE_TAG = "AUTH_TRACE"

class LoginViewModel(
    private val authApiService: AuthApiService,
    private val userSessionManager: UserSessionManager
) : ViewModel()
{
    companion object {
        private const val DEBUG_USERNAME = "jean.uwimana1"
        private const val DEBUG_PASSWORD = "pas001"
    }

    var usernameOrEmail by mutableStateOf(DEBUG_USERNAME)
        private set

    var password by mutableStateOf(DEBUG_PASSWORD)
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
        Log.d(
            AUTH_TRACE_TAG,
            "LoginViewModel.onUsernameOrEmailChange oldLength=${usernameOrEmail.length} newLength=${newValue.length}"
        )
        usernameOrEmail = newValue
        loginError = null
    }

    fun onPasswordChange(newValue: String) {
        Log.d(
            AUTH_TRACE_TAG,
            "LoginViewModel.onPasswordChange oldLength=${password.length} newLength=${newValue.length}"
        )
        password = newValue
        loginError = null
    }

    fun login() {
        Log.d(
            AUTH_TRACE_TAG,
            "LoginViewModel.login:invoked username='${usernameOrEmail}' usernameLength=${usernameOrEmail.length} passwordLength=${password.length}"
        )
        if (usernameOrEmail.isBlank() || password.isBlank()) {
            Log.d(AUTH_TRACE_TAG, "LoginViewModel.login:blocked blank credentials")
            loginError = "Username/Email and Password cannot be empty."
            return
        }

        isLoading = true
        loginError = null
        loginSuccess = null
        Log.d(AUTH_TRACE_TAG, "LoginViewModel.login:state isLoading=true loginError=null loginSuccess=null")

        viewModelScope.launch {
            try {
                val request = LoginRequestDto(usernameOrEmail, password)
                Log.d(
                    AUTH_TRACE_TAG,
                    "LoginViewModel.login:request created username='${request.username}' passwordLength=${request.password.length}"
                )
                Log.d(
                    "LOGIN_FLOW",
                    "Sending login request with username=${request.username}, passwordLength=${request.password.length}"
                )
                when (val result = authApiService.login(request)) {
                    is LoginResult.Success -> {
                        Log.d(
                            AUTH_TRACE_TAG,
                            "LoginViewModel.login:success response userId=${result.response.user.id} email=${result.response.user.email} roles=${result.response.user.roles}"
                        )
                        Log.d("LOGIN_FLOW", "Login response: ${result.response}")

                        // ✅ Protect this from cancellation due to navigation
                        val user = withContext(NonCancellable) {
                            Log.d(AUTH_TRACE_TAG, "LoginViewModel.login:calling saveLoginResponse")
                            userSessionManager.saveLoginResponse(result.response)
                        }

                        loginSuccess = user
                        Log.d(
                            AUTH_TRACE_TAG,
                            "LoginViewModel.login:loginSuccess assigned userId=${user.userId} currentRole=${user.currentRole} roles=${user.activeRoles}"
                        )
                        Log.d("LOGIN_FLOW", "Login complete. User: $user")
                    }
                    is LoginResult.Failure -> {
                        Log.d(
                            AUTH_TRACE_TAG,
                            "LoginViewModel.login:failure status=${result.statusCode} message=${result.message}"
                        )
                        Log.d(
                            "LOGIN_FLOW",
                            "Login refused with status=${result.statusCode}, message=${result.message}"
                        )
                        loginError = result.message
                    }
                }

            } catch (e: SerializationException) {
                Log.e(AUTH_TRACE_TAG, "LoginViewModel.login:SerializationException message=${e.message}", e)
                loginError = "Data format error. Please try again."
            } catch (e: IOException) {
                Log.e(AUTH_TRACE_TAG, "LoginViewModel.login:IOException message=${e.message}", e)
                loginError = e.message ?: "Unable to reach the server. Please try again."
            } catch (e: Exception) {
                Log.e(AUTH_TRACE_TAG, "LoginViewModel.login:UnexpectedException message=${e.message}", e)
                Log.e("LOGIN_FLOW", "Unexpected error", e)
                loginError = e.message ?: "An unexpected error occurred. Please try again."
            } finally {
                isLoading = false
                Log.d(
                    AUTH_TRACE_TAG,
                    "LoginViewModel.login:finally isLoading=false loginError=${loginError != null} loginSuccess=${loginSuccess != null}"
                )
            }
        }
    }

    fun resetState() {
        Log.d(
            AUTH_TRACE_TAG,
            "LoginViewModel.resetState before isLoading=$isLoading loginError=${loginError != null} loginSuccess=${loginSuccess != null}"
        )
        isLoading = false
        loginError = null
        loginSuccess = null
        Log.d(AUTH_TRACE_TAG, "LoginViewModel.resetState after isLoading=false loginError=null loginSuccess=null")
    }
}
