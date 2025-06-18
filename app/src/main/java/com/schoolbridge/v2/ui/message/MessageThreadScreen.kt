package com.schoolbridge.v2.ui.message

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.messaging.MessageThread
import java.time.format.DateTimeFormatter


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageThreadRepository
import com.schoolbridge.v2.ui.event.EventRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageThreadScreen(
    messageThreadId: String,
    onBack: () -> Unit,
    messageThreadRepository: MessageThreadRepository,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State to hold the loaded MessageThread
   var thread by remember { mutableStateOf<MessageThread?>(null) }
    var replyText by remember { mutableStateOf("") }
    val scrollState = rememberLazyListState()

    // Fetch thread when messageThreadId changes
    LaunchedEffect(messageThreadId) {
        Log.d("MESSAGE_THREAD","Mesage got data id $messageThreadId")
        thread = messageThreadRepository.getThreadById(messageThreadId)
        Log.d("MESSAGE_THREAD","Mesage got data $thread")
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                    Row(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = replyText,
                            onValueChange = { replyText = it },
                            placeholder = { Text("Type your reply...") },
                            modifier = Modifier.weight(1f),
                            maxLines = 4,
                            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                            keyboardActions = KeyboardActions(onSend = {
                                if (replyText.isNotBlank()) {
                                    onSendMessage(replyText.trim())
                                    replyText = ""
                                }
                            }),
                            //colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)
                        )
                        IconButton(
                            onClick = {
                                if (replyText.isNotBlank()) {
                                    onSendMessage(replyText.trim())
                                    replyText = ""
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
    ) { innerPadding ->

        if (thread == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EMPTY")
                }
                //CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                state = scrollState,
                contentPadding = innerPadding,
                modifier = Modifier.fillMaxSize(),
                reverseLayout = false
            ) {
                items(thread!!.messages) { message ->
                    MessageItem(message)
                }
            }
        }
    }
}


@Composable
fun MessageItem(message: Message) {
    val isSystem = message.isSystem ?: false

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
            Text(
                text = message.sender,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 2.dp)
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    if (message.attachments.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        message.attachments.forEach { att ->
                            Text(
                                text = "📎 $att",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary
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
