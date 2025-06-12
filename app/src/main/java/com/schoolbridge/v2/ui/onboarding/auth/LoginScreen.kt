package com.schoolbridge.v2.ui.onboarding.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.data.remote.AuthApiService
import com.schoolbridge.v2.data.remote.AuthApiServiceImpl
import com.schoolbridge.v2.ui.onboarding.auth.LoginViewModel
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import androidx.lifecycle.ViewModelProvider.Factory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigateToHome: () -> Unit ,//callback here
    // For now, we'll just show a Toast on success
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(AuthApiServiceImpl())) // Pass your service implementation
) {
    val context = LocalContext.current
    val isLoading = viewModel.isLoading
    val loginError = viewModel.loginError
    val loginSuccess = viewModel.loginSuccess

    // Effect for showing success/error messages as Toasts
    LaunchedEffect(loginSuccess) {
        loginSuccess?.let {
            Toast.makeText(context, "Login Successful! Welcome, ${it.firstName}", Toast.LENGTH_LONG).show()
            // Here you would typically navigate to the main screen
            navigateToHome()
            viewModel.resetState()
        }
    }

    LaunchedEffect(loginError) {
        loginError?.let {
            Log.d("ERROR_LOGIN",it)
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetState() // Optional: reset state on error to allow re-typing
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Login") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .imePadding(), // Adjusts padding for software keyboard
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = viewModel.usernameOrEmail,
                onValueChange = viewModel::onUsernameOrEmailChange,
                label = { Text("Username or Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null // Show error state if there's an error
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = viewModel::onPasswordChange,
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Lock Icon") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.login() } // Submit on Done key
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null // Show error state if there's an error
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::login,
                enabled = !isLoading, // Disable button while loading
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login", style = MaterialTheme.typography.titleMedium)
                }
            }

            // Optional: Error message display below button

            loginError?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { /* Handle forgot password */ }) {
                Text("Forgot Password?")
            }

            TextButton(onClick = { /* Handle sign up */ }) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}

// ViewModel Factory to provide the AuthApiService
class LoginViewModelFactory(private val authApiService: AuthApiService) : Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(authApiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    SchoolBridgeV2Theme {  // Use your app's theme for accurate preview
        LoginScreen(navigateToHome = {})
    }
}