package com.schoolbridge.v2.ui.message

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.ui.navigation.MainAppScreen

@Composable
fun MessageScreen(
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    onBack: () -> Unit,
    onMessageThreadClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            CustomBottomNavBar(
                currentScreen = currentScreen,
                onTabSelected = onTabSelected
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Display the inbox list. 
            // We pass onMessageThreadClick so the NavHost can handle the transition.
            MessageThreadScreen(
                initialThreadId = null,
                onThreadSelected = onMessageThreadClick
            )
        }
    }
}
