package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.VideoCall
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.Duration
import java.time.format.DateTimeFormatter

@Composable
fun AgendaCard(
    item: AgendaItemUi,
    density: AgendaDensity = AgendaDensity.COMFORTABLE,
    modifier: Modifier = Modifier
) {
    val accent = agendaAccent(item.kind)
    val icon = agendaIcon(item.kind)
    val timeLabel = rememberTimeLabel(item)
    val durationLabel = rememberDurationLabel(item)
    val compact = density == AgendaDensity.COMPACT
    val cardPadding = if (compact) 14.dp else 18.dp
    val spacing = if (compact) 10.dp else 14.dp
    val iconSize = if (compact) 42.dp else 50.dp

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isImportant) {
                MaterialTheme.colorScheme.surfaceContainer
            } else {
                MaterialTheme.colorScheme.surfaceContainerLow
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.Top
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(iconSize)
                        .background(accent.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accent,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = timeLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = durationLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AgendaBadge(item.badge, accent)
                    AgendaSource(item.sourceLabel)
                    item.statusLabel?.let { label ->
                        AgendaStatus(label = label, accent = accent, important = item.isImportant)
                    }
                }

                Text(
                    text = item.title,
                    style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                if (!item.note.isNullOrBlank()) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
                    ) {
                        Text(
                            text = item.note,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                item.ctaLabel?.let { cta ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(accent, CircleShape)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = cta,
                            style = MaterialTheme.typography.labelLarge,
                            color = accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendaBadge(label: String, accent: Color) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.16f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = accent
        )
    }
}

@Composable
private fun AgendaSource(label: String) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AgendaStatus(
    label: String,
    accent: Color,
    important: Boolean
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = if (important) accent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (important) accent else MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
private fun rememberTimeLabel(item: AgendaItemUi): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return "${item.start.format(formatter)} - ${item.end.format(formatter)}"
}

@Composable
private fun rememberDurationLabel(item: AgendaItemUi): String {
    val minutes = Duration.between(item.start, item.end).toMinutes()
    return if (minutes >= 60) {
        val hours = minutes / 60
        val rest = minutes % 60
        if (rest == 0L) "${hours}h" else "${hours}h ${rest}m"
    } else {
        "${minutes}m"
    }
}

@Composable
private fun agendaAccent(kind: AgendaItemKind): Color = when (kind) {
    AgendaItemKind.CLASS -> MaterialTheme.colorScheme.primary
    AgendaItemKind.ASSESSMENT -> MaterialTheme.colorScheme.error
    AgendaItemKind.MEETING -> MaterialTheme.colorScheme.tertiary
    AgendaItemKind.CALL -> MaterialTheme.colorScheme.secondary
    AgendaItemKind.ANNOUNCEMENT -> MaterialTheme.colorScheme.primary
}

@Composable
private fun agendaIcon(kind: AgendaItemKind): ImageVector = when (kind) {
    AgendaItemKind.CLASS -> Icons.Default.MenuBook
    AgendaItemKind.ASSESSMENT -> Icons.Default.CalendarMonth
    AgendaItemKind.MEETING -> Icons.Default.CalendarMonth
    AgendaItemKind.CALL -> Icons.Default.VideoCall
    AgendaItemKind.ANNOUNCEMENT -> Icons.Default.Campaign
}
