package com.schoolbridge.v2.ui.message

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.MessageThreadRepository
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    userSessionManager: UserSessionManager,
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    onBack: () -> Unit,
    onMessageThreadClick: (String) -> Unit,
    onInvitesClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    /* ---------- Data ---------- */
    val repo = remember { MessageThreadRepository() }
    val threads by repo.threads.collectAsState()
    val invitations by repo.invites.collectAsState()
    var inviteCount by remember {mutableIntStateOf( invitations.size)}
    var search by remember { mutableStateOf("") }


    /* ---------- UI ---------- */
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
                    IconButton(onClick = onInvitesClick) {
                        BadgedBox(
                            badge = {
                                if (inviteCount > 0) {
                                    Badge {
                                        Text(inviteCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.MailOutline, contentDescription = "Invites")
                        }
                    }

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
            /* --- Search --- */
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

            /* --- Threads --- */
            val filteredThreads = remember(search, threads) {
                if (search.isBlank()) threads
                else threads.filter { t ->
                    t.subject.contains(search, ignoreCase = true) ||
                            t.participants.any { it.contains(search, ignoreCase = true) }
                }
            }

            if (filteredThreads.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item("invite-header") {
                        AnimatedVisibility(
                            visible = inviteCount > 0,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            ElevatedCard(
                                onClick = onInvitesClick,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(16.dp)
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "You have $inviteCount thread invitation${if (inviteCount > 1) "s" else ""}",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text(
                                            text = "Tap to view and accept or decline",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                                }
                            }
                        }
                    }


                    items(
                        items = filteredThreads,
                        key = { it.id }
                    ) { thread ->
                        ThreadCard(
                            thread = thread,
                            onClick = { onMessageThreadClick(it.id) }
                        )
                    }


                }
            }
        }
    }
}

/* ---------- Optional Empty State UI ---------- */
@Composable
fun EmptyState() {
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
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "Start by tapping the Compose button.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
