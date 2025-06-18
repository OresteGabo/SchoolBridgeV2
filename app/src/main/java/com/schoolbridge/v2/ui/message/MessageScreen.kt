package com.schoolbridge.v2.ui.message

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.schoolbridge.v2.R
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.ThreadRepository
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.home.alert.AlertDetailsBottomSheetContent
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.ui.theme.onBackgroundDark
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    userSessionManager: UserSessionManager,
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Simulate repository load — replace with ViewModel later
    val threads by remember {
        mutableStateOf(ThreadRepository().threads.value) // For real app: collectAsState()
    }

    var search by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Compose new message */ }) {
                        Icon(Icons.Default.Add, contentDescription = "New")
                    }
                }
            )
        },
        bottomBar = {
            CustomBottomNavBar(
                currentScreen = currentScreen,
                onTabSelected = onTabSelected
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Compose new thread */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Compose")
            }
        },
        modifier = modifier
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- Search bar ---
            OutlinedTextField(
                value = search,
                onValueChange = { search = it },
                placeholder = { Text("Search threads…") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // --- Filtered threads ---
            val filteredThreads = remember(search, threads) {
                if (search.isBlank()) threads
                else threads.filter { thread ->
                    thread.subject.contains(search, ignoreCase = true) ||
                            thread.participants.any { it.contains(search, ignoreCase = true) }
                }
            }

            if (filteredThreads.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(64.dp)
                        )

                        Text(
                            text = "No messages yet 📭",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Column(
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            Text("💬 Each topic starts a new message box.", style = MaterialTheme.typography.bodyMedium)
                            Text("✏️ Tap the Compose button to begin.", style = MaterialTheme.typography.bodyMedium)
                            Text("📁 Old conversations may be removed over time.", style = MaterialTheme.typography.bodyMedium)
                            Text(
                                text = "👥 New participants will only see replies sent *after* they joined.",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                        }

                        Text(
                            text = "You're all caught up!",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }
                }
            }

            else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    threads.forEach { thread ->
                        item(key = thread.id) {
                            ThreadCard(thread, onClick = {
                                // TODO: handle click
                            })
                        }
                    }
                }
            }
        }
    }
}

