package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
@OptIn(ExperimentalFoundationApi::class)
fun AgendaCard(
    item: AgendaItemUi,
    density: AgendaDensity = AgendaDensity.COMFORTABLE,
    modifier: Modifier = Modifier
) {
    val accent = agendaAccent(item.kind)
    val icon = agendaIcon(item.kind)
    val timeLabel = rememberTimeLabel(item)
    val durationLabel = rememberDurationLabel(item)
    var expanded by rememberSaveable(item.id) { mutableStateOf(false) }
    val hasExpandableContent = item.subtitle.length > 88 || !item.note.isNullOrBlank()
    val compact = density == AgendaDensity.COMPACT
    val cardPadding = if (compact) 14.dp else 18.dp
    val spacing = if (compact) 10.dp else 14.dp
    val iconSize = if (compact) 40.dp else 48.dp
    val colorScheme = MaterialTheme.colorScheme
    val isPersonalPlan = item.origin == AgendaItemOrigin.PERSONAL_PLAN

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPersonalPlan) {
                colorScheme.surfaceContainerHigh.copy(alpha = 0.94f)
            } else if (item.isImportant) {
                colorScheme.surfaceBright
            } else {
                colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isPersonalPlan) 5.dp else if (item.isImportant) 8.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = if (isPersonalPlan) {
                        colorScheme.outlineVariant.copy(alpha = 0.4f)
                    } else {
                        accent.copy(alpha = if (item.isImportant) 0.22f else 0.1f)
                    },
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(cardPadding)
        ) {
            if (isPersonalPlan) {
                Surface(
                    modifier = Modifier.wrapContentWidth(),
                    shape = RoundedCornerShape(999.dp),
                    color = accent.copy(alpha = 0.14f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(accent, CircleShape)
                        )
                        Text(
                            text = "Your plan",
                            style = MaterialTheme.typography.labelMedium,
                            color = accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = if (isPersonalPlan) 10.dp else 0.dp),
                horizontalArrangement = Arrangement.spacedBy(spacing),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.width(if (compact) 78.dp else 88.dp),
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
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = durationLabel,
                        style = MaterialTheme.typography.labelSmall,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp)
                ) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AgendaBadge(item.badge, accent)
                        if (!isPersonalPlan) {
                            AgendaSource(
                                label = item.sourceLabel,
                                highlighted = item.isOwnedByCurrentUser
                            )
                        }
                        item.schoolName?.let { schoolName ->
                            AgendaSource(label = schoolName)
                        }
                        item.statusLabel?.let { label ->
                            AgendaStatus(label = label, accent = accent, important = item.isImportant)
                        }
                    }

                    Text(
                        text = item.title,
                        style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colorScheme.onSurface
                    )

                    Text(
                        text = item.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                    )

                    if (expanded && !item.note.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = colorScheme.surfaceVariant.copy(alpha = 0.54f)
                        ) {
                            Text(
                                text = item.note,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (item.ctaLabel != null || hasExpandableContent) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            item.ctaLabel?.let { cta ->
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = accent.copy(alpha = 0.14f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(accent, CircleShape)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = cta,
                                            style = MaterialTheme.typography.labelLarge,
                                            color = accent,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }

                            if (hasExpandableContent) {
                                TextButton(onClick = { expanded = !expanded }) {
                                    Text(if (expanded) "Show less" else "Show more")
                                }
                            } else {
                                Spacer(Modifier.width(1.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AgendaBadge(label: String, accent: Color) {
    Surface(
        modifier = Modifier.defaultMinSize(minHeight = 32.dp),
        shape = RoundedCornerShape(999.dp),
        color = accent.copy(alpha = 0.16f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = accent,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun AgendaSource(
    label: String,
    highlighted: Boolean = false
) {
    Surface(
        modifier = Modifier.defaultMinSize(minHeight = 32.dp),
        shape = RoundedCornerShape(999.dp),
        color = if (highlighted) {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.82f)
        } else {
            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f)
        }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (highlighted) {
                MaterialTheme.colorScheme.onPrimaryContainer
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
        modifier = Modifier.defaultMinSize(minHeight = 32.dp),
        shape = RoundedCornerShape(999.dp),
        color = if (important) accent.copy(alpha = 0.18f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = if (important) accent else MaterialTheme.colorScheme.onSecondaryContainer,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
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
    AgendaItemKind.PERSONAL -> MaterialTheme.colorScheme.secondary
}

@Composable
private fun agendaIcon(kind: AgendaItemKind): ImageVector = when (kind) {
    AgendaItemKind.CLASS -> Icons.Default.MenuBook
    AgendaItemKind.ASSESSMENT -> Icons.Default.CalendarMonth
    AgendaItemKind.MEETING -> Icons.Default.CalendarMonth
    AgendaItemKind.CALL -> Icons.Default.VideoCall
    AgendaItemKind.ANNOUNCEMENT -> Icons.Default.Campaign
    AgendaItemKind.PERSONAL -> Icons.Default.AutoStories
}
