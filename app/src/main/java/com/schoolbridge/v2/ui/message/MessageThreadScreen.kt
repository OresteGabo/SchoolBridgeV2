package com.schoolbridge.v2.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageThread
import java.time.format.DateTimeFormatter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun MessageThreadScreen() {
    val viewModel: MessageThreadViewModel = viewModel()
    val messageThreads by viewModel.messageThreads.collectAsState()
    var selectedThread by remember { mutableStateOf<MessageThread?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (messageThreads.isEmpty()) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messageThreads) { thread ->
                    ThreadCard(
                        thread = thread,
                        onClick = { selectedThread = thread },
                    )
                }
            }
        }

        // Dialog to show messages for the selected thread
        selectedThread?.let { thread ->
            AlertDialog(
                onDismissRequest = { selectedThread = null },
                title = { Text("Messages for ${thread.topic}") },
                text = {
                    LazyColumn {
                        items(thread.messages) { message ->
                            Text(
                                text = "[${message.timestamp}] ${message.sender}: ${message.content}",
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { selectedThread = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}






/*
@Composable
fun MessageItem(message: Message, isSystemThread: Boolean = false) {
    val isCurrentUser = message.sender == "You"
    val isSystemMessage = message.sender == "System" || isSystemThread

    val alignment = when {
        isSystemMessage -> Alignment.CenterHorizontally
        isCurrentUser -> Alignment.End
        else -> Alignment.Start
    }

    val backgroundColor = when {
        isSystemMessage -> MaterialTheme.colorScheme.surfaceVariant
        isCurrentUser -> MaterialTheme.colorScheme.primaryContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val textColor = when {
        isSystemMessage -> MaterialTheme.colorScheme.onSurfaceVariant
        isCurrentUser -> MaterialTheme.colorScheme.onPrimaryContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        if (!isCurrentUser && !isSystemMessage) {
            Text(
                text = message.sender,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, bottom = 4.dp)
            )
        }
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = backgroundColor,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .widthIn(max = 300.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(message.content, style = MaterialTheme.typography.bodyMedium, color = textColor)
                if (message.attachments.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    message.attachments.forEach {
                        Text("📎 $it", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
                Text(
                    text = message.timestamp.format(DateTimeFormatter.ofPattern("HH:mm, MMM d")),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.End).padding(top = 4.dp)
                )
            }
        }
    }
}
*/
