package com.schoolbridge.v2.domain.messaging

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.ui.message.EmptyState
/*
@Composable
fun MqttThreadViewer(
    threads: List<MessageThread>,
    onMessageThreadClick: (String) -> Unit
) = if (threads.isEmpty()) {
    EmptyState()
} else {
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = threads,
            key = { it.id }
        ) { thread ->
            ThreadCard(
                thread = thread,
                onClick = { onMessageThreadClick(thread.id) }
            )
        }
    }
}*/