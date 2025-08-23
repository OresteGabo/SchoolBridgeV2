package com.schoolbridge.v2.ui.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.messaging.MessageThread
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ThreadCard(
    thread: MessageThread,
    onClick: (MessageThread) -> Unit ,
    modifier: Modifier = Modifier
) {
    val accent = if (thread.getUnreadCount() > 0)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.outlineVariant

    ElevatedCard(
        onClick = { onClick(thread) },
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Row(
            Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Leading coloured bar (unread indicator)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent)
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = thread.topic,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                thread.getLatestMessage()?.let { message ->
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "participants here", // Placeholder for participants
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                thread.getLatestMessage()?.let { message ->
                    // Convert timestamp to LocalDateTime and format
                    val instant = Instant.ofEpochMilli(message.timestamp.toLong())
                    val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                    val formatter = DateTimeFormatter.ofPattern("MMM d, HH:mm")
                    Text(
                        text = dateTime.format(formatter),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (thread.getUnreadCount() > 0) {
                    Spacer(Modifier.height(4.dp))
                    Badge(
                        containerColor = MaterialTheme.colorScheme.primary
                    ) { Text("${thread.getUnreadCount()}") }
                }
            }
        }
    }
}