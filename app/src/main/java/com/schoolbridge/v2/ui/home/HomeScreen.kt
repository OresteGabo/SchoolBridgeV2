package com.schoolbridge.v2.ui.home // Adjust package as needed

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.data.session.UserSessionManager // Import your UserSessionManager

@OptIn(ExperimentalMaterial3Api::class) // Required for TopAppBar
@Composable
fun HomeScreen(
    userSessionManager: UserSessionManager,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    // Collect the currentUser StateFlow directly
    //Collect the currentUser StateFlow directly, providing an initial value of null
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)



    // Determine the display name
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SchoolBridge") }, // A more general app title
                actions = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from Scaffold
                .padding(16.dp), // Add additional padding for content
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome to SchoolBridge!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(16.dp))

            // Display personalized welcome if currentUser is available

            Text(
                text = "Hello, ${currentUser?.firstName}!", // Personalized greeting
                style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Email: ${currentUser?.email}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Your roles: ${currentUser?.activeRoles?.joinToString(", ")}",
                style = MaterialTheme.typography.bodyMedium
            )


            Spacer(Modifier.height(32.dp))
            Text(
                text = "Explore your personalized dashboard here.",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}