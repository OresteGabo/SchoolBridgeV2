package com.schoolbridge.v2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.schoolbridge.v2.data.remote.AuthApiServiceImpl
import com.schoolbridge.v2.data.session.SessionState
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.ui.home.SplashScreen
import com.schoolbridge.v2.ui.navigation.AppNavHost
import com.schoolbridge.v2.ui.navigation.AuthScreen
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.ui.onboarding.OnboardingScreen
import com.schoolbridge.v2.ui.home.timetable.ScheduleReminderScheduler
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import com.schoolbridge.v2.ui.theme.ThemeViewModel

class MainActivity : ComponentActivity() {
    companion object {
        const val EXTRA_OPEN_CONVERSATION_ID = "extra_open_conversation_id"
        const val EXTRA_OPEN_CALL_MESSAGE_ID = "extra_open_call_message_id"
        const val EXTRA_OPEN_SCHEDULE = "extra_open_schedule"
    }

    /* simple manual DI ---------------------------------------------------- */
    private val authApiService        = AuthApiServiceImpl()
    private lateinit var userSessionManager: UserSessionManager
    /* -------------------------------------------------------------------- */
    private var pendingConversationId by mutableStateOf<String?>(null)
    private var pendingCallMessageId by mutableStateOf<String?>(null)
    private var openScheduleRequested by mutableStateOf(false)

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        userSessionManager     = UserSessionManager(applicationContext)
        consumeNavigationIntent(intent)

        setContent {
            /* --- theme state from ViewModel ------------------------------ */
            val themeViewModel: ThemeViewModel = viewModel()

            val isDarkTheme by themeViewModel.isDark.collectAsState()
            val palette     by themeViewModel.palette.collectAsState()
            val contrast    by themeViewModel.contrast.collectAsState()

            /* --- session state ------------------------------------------ */
            val sessionMgr         = remember { userSessionManager }
            val sessionState by sessionMgr.sessionState.collectAsState()
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission()
            ) { }

            LaunchedEffect(Unit) {
                Log.d("MainActivity", "Launching session initialization…")
                sessionMgr.initializeSession()
            }

            LaunchedEffect(sessionState) {
                if (sessionState is SessionState.LoggedIn) {
                    ScheduleReminderScheduler.start(applicationContext)
                    if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                        ContextCompat.checkSelfPermission(
                            applicationContext,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
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
                                themeViewModel         = themeViewModel
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
                                pendingMessageConversationId = pendingConversationId,
                                pendingCallMessageId = pendingCallMessageId,
                                openScheduleRequested = openScheduleRequested,
                                onPendingNotificationConsumed = {
                                    pendingConversationId = null
                                    pendingCallMessageId = null
                                    openScheduleRequested = false
                                }
                            )
                        }
                    }

                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        consumeNavigationIntent(intent)
    }

    private fun consumeNavigationIntent(intent: Intent?) {
        pendingConversationId = intent?.getStringExtra(EXTRA_OPEN_CONVERSATION_ID)
        pendingCallMessageId = intent?.getStringExtra(EXTRA_OPEN_CALL_MESSAGE_ID)
        openScheduleRequested = intent?.getBooleanExtra(EXTRA_OPEN_SCHEDULE, false) == true
    }
}
