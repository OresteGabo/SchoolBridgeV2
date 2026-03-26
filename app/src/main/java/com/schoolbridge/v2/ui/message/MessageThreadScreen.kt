package com.schoolbridge.v2.ui.message

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageThread

@Composable
fun MessageThreadScreen(
    initialThreadId: String? = null,
    onThreadSelected: ((String) -> Unit)? = null,
    onBack: (() -> Unit)? = null
) {
    val viewModel: MessageThreadViewModel = viewModel()
    val messageThreads by viewModel.messageThreads.collectAsState()
    
    var internalSelectedThreadId by remember { mutableStateOf<String?>(null) }
    var viewingMessage by remember { mutableStateOf<Message?>(null) }
    
    val effectiveThreadId = initialThreadId ?: internalSelectedThreadId
    val selectedThread = messageThreads.find { it.id == effectiveThreadId }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(targetState = viewingMessage != null, label = "ViewTransition") { isViewingMessage ->
            if (isViewingMessage && viewingMessage != null) {
                MessageDetailsScreen(
                    message = viewingMessage!!,
                    onBack = { viewingMessage = null },
                    onDelete = { 
                        // TODO: Implement actual delete logic in ViewModel
                        viewingMessage = null 
                    },
                    onActionClick = { actionId ->
                        if (selectedThread != null) {
                            viewModel.performAction(selectedThread.id, viewingMessage!!.id, actionId)
                            // Refresh local viewingMessage status
                            viewingMessage = viewingMessage!!.copy(status = "Action Taken") 
                        }
                    }
                )
            } else {
                AnimatedContent(targetState = selectedThread, label = "ThreadTransition") { thread ->
                    if (thread == null) {
                        ThreadListScreen(
                            threads = messageThreads,
                            onThreadClick = { 
                                if (onThreadSelected != null) {
                                    onThreadSelected(it.id)
                                } else {
                                    internalSelectedThreadId = it.id
                                }
                                viewModel.markAsRead(it.id)
                            }
                        )
                    } else {
                        ThreadDetailScreen(
                            thread = thread,
                            onBack = { 
                                if (onBack != null) onBack() else internalSelectedThreadId = null 
                            },
                            onMessageClick = { viewingMessage = it },
                            onActionClick = { msgId, actionId -> 
                                viewModel.performAction(thread.id, msgId, actionId)
                            }
                        )
                    }
                }
            }
        }
    }

    // Back button handling
    BackHandler(enabled = viewingMessage != null || (onBack == null && internalSelectedThreadId != null)) {
        if (viewingMessage != null) {
            viewingMessage = null
        } else if (onBack == null && internalSelectedThreadId != null) {
            internalSelectedThreadId = null
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadListScreen(
    threads: List<MessageThread>,
    onThreadClick: (MessageThread) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        if (threads.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No messages yet", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding)
            ) {
                items(threads) { thread ->
                    ThreadCard(
                        thread = thread,
                        onClick = onThreadClick
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreadDetailScreen(
    thread: MessageThread,
    onBack: () -> Unit,
    onMessageClick: (Message) -> Unit,
    onActionClick: (String, String) -> Unit
) {
    val listState = rememberLazyListState()

    LaunchedEffect(thread.messages.size) {
        if (thread.messages.isNotEmpty()) {
            listState.animateScrollToItem(thread.messages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(thread.participantsLabel, style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /* More options */ }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(thread.messages) { message ->
                MessageCard(
                    message = message,
                    onClick = { onMessageClick(message) },
                    onActionClick = { actionId -> onActionClick(message.id, actionId) }
                )
            }
        }
    }
}

@Composable
fun MessageCard(
    message: Message,
    onClick: () -> Unit,
    onActionClick: (String) -> Unit
) {
    val isSystem = message.sender != "You"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isSystem) Alignment.Start else Alignment.End
    ) {
        ElevatedCard(
            onClick = onClick,
            modifier = Modifier.widthIn(max = 340.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (message.title != null) {
                    Text(
                        text = message.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                }
                
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 4 // Snippet view
                )
                
                if (message.status != null) {
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "✓ ${message.status}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }

                if (message.actions.isNotEmpty() && message.status == null) {
                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        message.actions.forEach { action ->
                            Button(
                                onClick = { onActionClick(action.actionId) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(action.label, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = message.sender,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        text = message.timestamp.split(", ").lastOrNull() ?: "",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
