package com.schoolbridge.v2.ui.home.alert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertSeverity
import java.time.format.DateTimeFormatter

/**
 * Compact card for a single alert.
 * Maintains card consistency with a vertical accent bar, using the theme's secondary colors
 * to differentiate from event cards. Includes an icon for clear alert semantics.
 *
 * @param message Alert message to display.
 * @param index Used for staggered animation delay.
 * @param modifier Modifier applied to the card.
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AlertCardCompact(
    alert: Alert,
    index: Int,
    onClick: (Alert) -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val severityColor = when (alert.severity) {
        AlertSeverity.HIGH -> MaterialTheme.colorScheme.error
        AlertSeverity.MEDIUM -> MaterialTheme.colorScheme.secondary
        AlertSeverity.LOW -> MaterialTheme.colorScheme.tertiary
    }

    val accentColor = if (alert.isRead)
        severityColor.copy(alpha = 0.35f)
    else
        severityColor

    val textColor = if (alert.isRead)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(500)) + slideInVertically(
            initialOffsetY = { it / 2 },
            animationSpec = tween(500, delayMillis = index * 70)
        )
    ) {
        Card(
            onClick = { onClick(alert) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 4.dp)
        ) {
            Row(modifier = Modifier.fillMaxSize()) {
                // Vertical accent bar (matches EventCardCompact)
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            accentColor,
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = textColor,
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(end = 6.dp)
                            )

                            Text(
                                text = alert.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = textColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            if (!alert.isRead) {
                                Text(
                                    text = "NEW",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier
                                        .background(
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = alert.message,
                            style = MaterialTheme.typography.bodySmall,
                            color = textColor.copy(alpha = 0.9f),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalance,
                                    contentDescription = "Source",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = alert.source,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = MaterialTheme.colorScheme.primary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Box(
                                modifier = Modifier.width(110.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Text(
                                    text = alert.timestamp.format(DateTimeFormatter.ofPattern("HH:mm, MMM d")),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
