// src/main/java/com/schoolbridge/v2/MainActivity.kt
package com.schoolbridge.v2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.schoolbridge.v2.ui.navigation.AppNavHost
import com.schoolbridge.v2.ui.navigation.AuthScreen // Import AuthScreen
import com.schoolbridge.v2.ui.navigation.MainAppScreen // Import MainAppScreen
import com.schoolbridge.v2.ui.theme.SchoolBridgeV2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SchoolBridgeV2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Determine the start destination based on login status or onboarding completion
                    // This is a simplified example; in a real app, you'd use a ViewModel or state
                    val startDestination = remember {
                        // For demonstration: Start with login.
                        // In a real app, you might check SharedPreferences/DataStore for a token
                        // or an onboarding flag.
                        // if (isUserLoggedIn) MainAppScreen.Home.route else AuthScreen.Login.route
                        AuthScreen.Login.route // Or AuthScreen.Onboarding.route for first-time users
                    }

                    val navController = rememberNavController()
                    AppNavHost(navController = navController, startDestination = startDestination)
                }
            }
        }
    }
}
