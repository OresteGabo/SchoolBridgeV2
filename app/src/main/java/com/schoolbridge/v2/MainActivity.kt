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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.schoolbridge.v2.ui.navigation.AppNavHost
import com.schoolbridge.v2.ui.navigation.AuthScreen
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.data.remote.AuthApiServiceImpl
import com.schoolbridge.v2.data.preferences.ThemePreferenceManager
import com.schoolbridge.v2.ui.theme.ThemeViewModel

// Removed Hilt imports
// import dagger.hilt.android.AndroidEntryPoint
// import javax.inject.Inject

// Ktor imports are no longer explicitly needed in MainActivity for AuthApiServiceImpl
// if AuthApiServiceImpl itself handles its HttpClient.
// import io.ktor.client.HttpClient
// import io.ktor.client.engine.android.Android
// import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
// import io.ktor.serialization.kotlinx.json.json
// import kotlinx.serialization.json.Json

// Removed kotlinx.coroutines imports if not used directly here outside of LaunchedEffect
// import kotlinx.coroutines.CoroutineScope
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.launch


// Removed @AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var authApiService: AuthApiServiceImpl
    private lateinit var userSessionManager: UserSessionManager
    private lateinit var themePreferenceManager: ThemePreferenceManager

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivity", "onCreate called.")

        // Manual Dependency Instantiation
        authApiService = AuthApiServiceImpl()
        userSessionManager = UserSessionManager(applicationContext)
        themePreferenceManager = ThemePreferenceManager(applicationContext)

        setContent {
            Log.d("MainActivity", "setContent lambda entered.")

            val currentAuthApiService = remember { authApiService }
            val currentUserSessionManager = remember { userSessionManager }
            val currentThemePreferenceManager = remember { themePreferenceManager }

            val themeViewModel: ThemeViewModel = viewModel()

            val isDarkTheme by themeViewModel.isDarkTheme.collectAsState(initial = false)
            Log.d("MainActivity", "isDarkTheme collected from ThemeViewModel: $isDarkTheme")

            val isLoggedIn by currentUserSessionManager.isLoggedIn.collectAsState(initial = false)
            Log.d("MainActivity", "isLoggedIn collected: $isLoggedIn")

            LaunchedEffect(Unit) {
                Log.d("MainActivity", "LaunchedEffect started.")
                currentUserSessionManager.initializeSession()
                Log.d("MainActivity", "userSessionManager.initializeSession() called inside LaunchedEffect.")
            }

            val startDestination = remember(isLoggedIn) {
                Log.d("MainActivity", "Determining startDestination. isLoggedIn: $isLoggedIn")
                if (isLoggedIn) MainAppScreen.Home.route else AuthScreen.Login.route
            }

            SchoolBridgeV2Theme(
                isDarkTheme = isDarkTheme,
                dynamicColor = false
            ) {
                val navController = rememberNavController()
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavHost(
                        navController = navController,
                        startDestination = startDestination,
                        authApiService = currentAuthApiService,
                        userSessionManager = currentUserSessionManager,
                        themeViewModel = themeViewModel, // Pass themeViewModel down here
                        themePreferenceManager = currentThemePreferenceManager // optional if you want to use it elsewhere
                    )
                }
            }
        }
    }
}

