package com.schoolbridge.v2

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.schoolbridge.v2.data.preferences.ThemePreferenceManager
import com.schoolbridge.v2.data.remote.AuthApiServiceImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.data.session.SessionState // 👈 Make sure you define this sealed class
import com.schoolbridge.v2.ui.home.SplashScreen
import com.schoolbridge.v2.ui.navigation.AppNavHost
import com.schoolbridge.v2.ui.navigation.AuthScreen
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import com.schoolbridge.v2.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    private lateinit var authApiService: AuthApiServiceImpl
    private lateinit var userSessionManager: UserSessionManager
    private lateinit var themePreferenceManager: ThemePreferenceManager

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Manual dependencies
        authApiService = AuthApiServiceImpl()
        userSessionManager = UserSessionManager(applicationContext)
        themePreferenceManager = ThemePreferenceManager(applicationContext)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)

            val currentSessionManager = remember { userSessionManager }
            val sessionState by currentSessionManager.sessionState.collectAsState()

            LaunchedEffect(Unit) {
                Log.d("MainActivity", "Launching session initialization...")
                currentSessionManager.initializeSession()
            }

            SchoolBridgeV2Theme(isDarkTheme = isDarkTheme, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (val state = sessionState) {
                        is SessionState.Loading -> SplashScreen()
                        is SessionState.LoggedOut -> {
                            val navController = rememberNavController()
                            AppNavHost(
                                navController = navController,
                                startDestination = AuthScreen.Login.route,
                                authApiService = authApiService,
                                userSessionManager = currentSessionManager,
                                themeViewModel = themeViewModel,
                                themePreferenceManager = themePreferenceManager
                            )
                        }

                        is SessionState.LoggedIn -> {
                            val navController = rememberNavController()
                            AppNavHost(
                                navController = navController,
                                startDestination = MainAppScreen.Home.route,
                                authApiService = authApiService,
                                userSessionManager = currentSessionManager,
                                themeViewModel = themeViewModel,
                                themePreferenceManager = themePreferenceManager
                            )
                        }
                    }
                }
            }
        }
    }
}
