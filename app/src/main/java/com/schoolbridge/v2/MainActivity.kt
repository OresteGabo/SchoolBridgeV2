package com.schoolbridge.v2

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.schoolbridge.v2.data.preferences.ThemePreferenceManager
import com.schoolbridge.v2.data.remote.AuthApiServiceImpl
import com.schoolbridge.v2.data.session.SessionState
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.ui.home.SplashScreen
import com.schoolbridge.v2.ui.navigation.AppNavHost
import com.schoolbridge.v2.ui.navigation.AuthScreen
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.ui.onboarding.OnboardingScreen
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import com.schoolbridge.v2.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {

    /* simple manual DI ---------------------------------------------------- */
    private val authApiService        = AuthApiServiceImpl()
    private lateinit var userSessionManager: UserSessionManager
    private lateinit var themePreferenceManager: ThemePreferenceManager
    /* -------------------------------------------------------------------- */

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userSessionManager     = UserSessionManager(applicationContext)
        themePreferenceManager = ThemePreferenceManager(applicationContext)

        setContent {
            /* --- theme state from ViewModel ------------------------------ */
            val themeViewModel: ThemeViewModel = viewModel()

            val isDarkTheme by themeViewModel.isDark.collectAsState()
            val palette     by themeViewModel.palette.collectAsState()
            val contrast    by themeViewModel.contrast.collectAsState()

            /* --- session state ------------------------------------------ */
            val sessionMgr         = remember { userSessionManager }
            val sessionState by sessionMgr.sessionState.collectAsState()

            LaunchedEffect(Unit) {
                Log.d("MainActivity", "Launching session initialization…")
                sessionMgr.initializeSession()
            }

            /* --- top-level theme wrapper -------------------------------- */
            SchoolBridgeV2Theme(
                isDarkTheme  = isDarkTheme,
                palette      = palette,
                contrast     = contrast,
                dynamicColor = false      // or expose this via prefs later
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color    = MaterialTheme.colorScheme.background
                ) {
                    when (sessionState) {
                        is SessionState.Loading   -> SplashScreen()

                        is SessionState.Onboarding -> {
                            OnboardingScreen(
                                onFinished = {
                                    // Mark onboarding complete
                                    LaunchedEffect(Unit) {
                                        sessionMgr.markOnboardingComplete()
                                        sessionMgr.initializeSession() // triggers recomposition to switch to Auth
                                    }
                                }
                            )
                        }

                        is SessionState.LoggedOut -> {
                            val navController = rememberNavController()
                            AppNavHost(
                                navController          = navController,
                                startDestination       = AuthScreen.Login.route,
                                authApiService         = authApiService,
                                userSessionManager     = sessionMgr,
                                themeViewModel         = themeViewModel,
                                themePreferenceManager = themePreferenceManager
                            )
                        }

                        is SessionState.LoggedIn -> {
                            val navController = rememberNavController()
                            AppNavHost(
                                navController          = navController,
                                startDestination       = MainAppScreen.Home.route,
                                authApiService         = authApiService,
                                userSessionManager     = sessionMgr,
                                themeViewModel         = themeViewModel,
                                themePreferenceManager = themePreferenceManager
                            )
                        }
                    }

                }
            }
        }
    }
}
