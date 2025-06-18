package com.schoolbridge.v2.ui.message

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars // Import for WindowInsets.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding // Import for windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageThread
import com.schoolbridge.v2.domain.messaging.MessageThreadRepository
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageThreadScreen(
    messageThreadId: String,
    onBack: () -> Unit,
    messageThreadRepository: MessageThreadRepository,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var thread by remember { mutableStateOf<MessageThread?>(null) }
    var replyText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(messageThreadId) {
        thread = messageThreadRepository.getThreadById(messageThreadId)
    }

    Scaffold(
        // The Scaffold's topBar slot is inherently designed to stay fixed at the top.
        // We ensure its padding accounts for system bars (status bar, notch)
        topBar = {
            TopAppBar(
                // Use WindowInsets.systemBars for robust handling of status bar and display cutouts.
                // This modifier ensures the TopAppBar is placed correctly and doesn't get cut off.
                // It will remain fixed at the top if android:windowSoftInputMode="adjustResize" is used.
                modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
                title = {
                    if (thread != null) {
                        Column {
                            Text(thread!!.subject, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text(
                                text = "Participants: ${thread!!.participants.joinToString()}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    } else {
                        Text("Loading...")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (thread != null) {
                Surface(
                    tonalElevation = 4.dp,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            // navigationBarsPadding handles system navigation bar area
                            .navigationBarsPadding()
                            // imePadding handles the keyboard's height, pushing this composable up.
                            // This is crucial for preventing the TextField from being covered.
                            .imePadding()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextField(
                                value = replyText,
                                onValueChange = { replyText = it },
                                placeholder = { Text("Type your reply...") },
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(24.dp)),
                                shape = RoundedCornerShape(24.dp),
                                maxLines = 4,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                                keyboardActions = KeyboardActions(onSend = {
                                    if (replyText.isNotBlank()) {
                                        onSendMessage(replyText.trim())
                                        replyText = ""
                                        keyboardController?.hide() // Hide keyboard after sending
                                    }
                                })
                            )
                            Spacer(Modifier.width(8.dp))
                            IconButton(
                                onClick = {
                                    if (replyText.isNotBlank()) {
                                        onSendMessage(replyText.trim())
                                        replyText = ""
                                        keyboardController?.hide() // Hide keyboard after sending
                                    }
                                },
                                enabled = replyText.isNotBlank()
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        // The `innerPadding` provided by Scaffold now correctly includes space for:
        // - TopAppBar
        // - BottomBar (which includes navigation bars + IME padding due to .imePadding() on it)
        // This ensures the LazyColumn content adjusts correctly.
        if (thread == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding), // Apply padding from Scaffold
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = scrollState,
                // Apply the innerPadding to the content of the Scaffold.
                // This ensures messages are visible and don't go under bars/keyboard.
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true // Newest messages at the bottom
            ) {
                // Ensure messages are sorted oldest to newest for reverseLayout = true.
                // This means the oldest message is at the bottom, and the newest is at the top of the list
                // visually appearing at the bottom of the screen.
                items(thread!!.messages.sortedBy { it.timestamp }) { message ->
                    MessageItem(message)
                }
            }

            // Scroll to the newest message (index 0 when reverseLayout is true)
            // This ensures that when new messages load or are sent, the view is at the bottom.
            LaunchedEffect(thread?.messages?.size) {
                if (thread != null && thread!!.messages.isNotEmpty()) {
                    scrollState.scrollToItem(0)
                }
            }
        }
    }
}


@Composable
fun MessageItem(message: Message) {
    val isSystem = message.isSystem == true

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        if (isSystem) {
            // System messages with subtle style
            Text(
                text = message.content,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            // Determine if the message is from the current user for alignment and styling.
            // Replace "You" with actual logic to identify the current user (e.g., comparing sender ID).
            val isCurrentUser = message.sender == "You" // Placeholder: Implement your actual user check
            val alignment = if (isCurrentUser) Alignment.End else Alignment.Start
            val surfaceColor = if (isCurrentUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
            val onSurfaceColor = if (isCurrentUser) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = alignment
            ) {
                // Only show sender's name if it's not the current user's message
                if (!isCurrentUser) {
                    Text(
                        text = message.sender,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = surfaceColor,
                    modifier = Modifier.widthIn(max = 280.dp) // Constrain message bubble width for better readability
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = message.content,
                            style = MaterialTheme.typography.bodyMedium,
                            color = onSurfaceColor
                        )
                        if (message.attachments.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            message.attachments.forEach { att ->
                                Text(
                                    text = "📎 $att",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary // Or adjust color as needed
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm, MMM d")),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}