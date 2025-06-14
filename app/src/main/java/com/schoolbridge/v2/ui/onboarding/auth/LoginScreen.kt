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
import androidx.lifecycle.viewmodel.compose.viewModel

import com.schoolbridge.v2.data.remote.AuthApiService // Import AuthApiService
import com.schoolbridge.v2.data.session.UserSessionManager // Import UserSessionManager
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigateToHome: () -> Unit,
    authApiService: AuthApiService,
    userSessionManager: UserSessionManager,
    onForgotPassword: () -> Unit,
    onCreateAccount: () -> Unit,
    viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(authApiService, userSessionManager))
) {
    val context = LocalContext.current
    val isLoading = viewModel.isLoading
    val loginError = viewModel.loginError
    val loginSuccess = viewModel.loginSuccess

    //val isSessionReady by viewModel.isSessionFullyReady.collectAsState()


    LaunchedEffect(loginSuccess) {
        loginSuccess?.let {
            Toast.makeText(context, "Login Successful! Welcome, ${it.firstName}", Toast.LENGTH_LONG).show()
            navigateToHome()
            viewModel.resetState()
        }
    }

    LaunchedEffect(loginError) {
        loginError?.let {
            Log.d("ERROR_LOGIN", it)
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.resetState()
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
                .imePadding(),
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
                isError = loginError != null
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
                    onDone = { viewModel.login() }
                ),
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                isError = loginError != null
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = viewModel::login,
                enabled = !isLoading,
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

            loginError?.let { errorMessage ->
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = onForgotPassword) {
                Text("Forgot Password?")
            }

            TextButton(onClick = onCreateAccount) {
                Text("Don't have an account? Sign Up")
            }
        }
    }
}




