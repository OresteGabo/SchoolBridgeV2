package com.schoolbridge.v2.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

data class FriendlyNetworkMessage(
    val title: String,
    val message: String,
    val helperText: String,
    val icon: ImageVector
)

@Composable
fun FriendlyNetworkErrorCard(
    rawMessage: String?,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val content = rememberFriendlyNetworkMessage(rawMessage)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = content.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(34.dp)
                )
            }

            Text(
                text = content.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = content.message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Text(
                text = content.helperText,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            onRetry?.let {
                Button(onClick = it) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text("Try again")
                }
            }
        }
    }
}

@Composable
fun rememberFriendlyNetworkMessage(rawMessage: String?): FriendlyNetworkMessage {
    val normalized = rawMessage.orEmpty().trim()
    val lower = normalized.lowercase()

    return when {
        lower.contains("session") || lower.contains("sign in again") || lower.contains("401") -> FriendlyNetworkMessage(
            title = "Please sign in again",
            message = "Your SchoolBridge session is no longer active, so we could not open this page securely.",
            helperText = "Go back to login, then try again.",
            icon = Icons.Default.Lock
        )

        lower.contains("permission") || lower.contains("403") || lower == "forbidden" -> FriendlyNetworkMessage(
            title = "This page is not available for this account",
            message = "You are signed in, but this action is not allowed for your current role or access level.",
            helperText = "Try switching role, refreshing your account, or asking the school to validate your access.",
            icon = Icons.Default.Lock
        )

        lower.contains("timed out") || lower.contains("timeout") -> FriendlyNetworkMessage(
            title = "The server is taking too long",
            message = "We reached the server, but it did not answer in time.",
            helperText = "Check your connection and try again in a moment.",
            icon = Icons.Default.Schedule
        )

        lower.contains("internet") || lower.contains("reach the server") || lower.contains("connection") || lower.contains("offline") -> FriendlyNetworkMessage(
            title = "No connection to SchoolBridge",
            message = "The app could not reach the server right now.",
            helperText = "Check your internet or Wi-Fi, then try again.",
            icon = Icons.Default.CloudOff
        )

        lower.contains("not find") || lower.contains("not found") || lower.contains("404") -> FriendlyNetworkMessage(
            title = "We could not find that information",
            message = "The requested record is missing or no longer available.",
            helperText = "Refresh the page or return and open it again.",
            icon = Icons.Default.ReportProblem
        )

        lower.contains("server") || lower.contains("500") || lower.contains("503") -> FriendlyNetworkMessage(
            title = "SchoolBridge is having trouble",
            message = "The server ran into a problem while loading this page.",
            helperText = "Please wait a little and try again.",
            icon = Icons.Default.ReportProblem
        )

        normalized.isBlank() -> FriendlyNetworkMessage(
            title = "Something did not load",
            message = "We could not finish this request right now.",
            helperText = "Please try again.",
            icon = Icons.Default.ReportProblem
        )

        else -> FriendlyNetworkMessage(
            title = "Something needs your attention",
            message = normalized,
            helperText = "If this keeps happening, refresh the page or try again later.",
            icon = Icons.Default.ReportProblem
        )
    }
}
