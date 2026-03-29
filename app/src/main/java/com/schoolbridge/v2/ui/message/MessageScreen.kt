package com.schoolbridge.v2.ui.message

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import com.schoolbridge.v2.R
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.AdaptivePageFrame
import com.schoolbridge.v2.ui.navigation.MainAppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    userSessionManager: UserSessionManager,
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    onBack: () -> Unit,
    onMessageConversationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val currentUser by userSessionManager.currentUser.collectAsState(initial = null)

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text  = t(R.string.messages_title),
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text  = t(R.string.messages_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Search */ }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = { /* TODO: Filter */ }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filter",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor        = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor     = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            CustomBottomNavBar(
                currentScreen = currentScreen,
                onTabSelected = onTabSelected,
                currentUser = currentUser
            )
        }
    ) { innerPadding ->
        AdaptivePageFrame(
            modifier = Modifier.fillMaxSize(),
            contentPadding = innerPadding
        ) {
            MessageConversationScreen(
                userSessionManager = userSessionManager,
                initialConversationId  = null,
                onConversationSelected = onMessageConversationClick
            )
        }
    }
}
