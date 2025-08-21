package com.schoolbridge.v2.ui.message

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.ui.navigation.MainAppScreen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AddComment
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.components.CustomBottomNavBar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.schoolbridge.v2.domain.messaging.MqttThreadRepository
import com.schoolbridge.v2.domain.messaging.MqttThreadViewer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    onBack: () -> Unit,
    onMessageThreadClick: (String) -> Unit,
    onInvitesClick: () -> Unit = {},
    onContactsClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // ✅ Get context from Compose
    //val context = LocalContext.current
    val repo = remember { MqttThreadRepository() }
    val threads by repo.threads.collectAsState()

    val inviteCount = 5 // TODO: replace with repo.invites.collectAsState() if you add invites

    var search by remember { mutableStateOf("") }
    var searchMode by remember { mutableStateOf(false) }

    val filteredThreads = remember(search, threads) {
        if (search.isBlank()) threads
        else threads.filter {
            it.subject.contains(search, ignoreCase = true) ||
                    it.participants.any { name -> name.contains(search, ignoreCase = true) }
        }
    }

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(searchMode) {
        if (searchMode) focusRequester.requestFocus()
    }

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
                    IconButton(onClick = onContactsClick) {
                        Icon(Icons.Default.ContactMail, contentDescription = "Contacts")
                    }
                    if (!searchMode) {
                        IconButton(onClick = { searchMode = true }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
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
                Icon(Icons.Default.AddComment, contentDescription = "Compose")
            }
        },
        modifier = modifier
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // --- Animated Search ---
            AnimatedVisibility(visible = searchMode) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        placeholder = { Text("Search threads…") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester)
                    )
                    IconButton(onClick = {
                        searchMode = false
                        search = ""
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel Search")
                    }
                }
            }

            // --- Animated Invite Card ---
            AnimatedVisibility(
                visible = inviteCount > 0 && !searchMode,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                InviteBanner(
                    inviteCount = inviteCount,
                    onClick = onInvitesClick
                )
            }

            Box(modifier = Modifier.padding(bottom = 8.dp)) {
                MqttThreadViewer(
                    threads = filteredThreads, // ✅ use filtered, not raw threads
                    onMessageThreadClick = onMessageThreadClick
                )
            }
        }
    }
}


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
                text = "💡 Tip: Use the search bar to find threads by subject, message content, or participant names.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun InviteBanner(
    inviteCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (inviteCount <= 0) return

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "You have $inviteCount thread invitation${if (inviteCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                //InviteChipNotification(inviteCount = inviteCount, onClick = onClick)
                Text(
                    text = "Tap to respond to the request",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}




