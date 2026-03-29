package com.schoolbridge.v2.ui.home.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
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
    modifier: Modifier = Modifier,
    onCTAClick: () -> Unit = {},
    onCardClick: () -> Unit = {}
) {
    val accent = agendaAccent(item.kind)
    val colorScheme = MaterialTheme.colorScheme
    val compact = density == AgendaDensity.COMPACT
    val hasExpandableContent = item.subtitle.length > 88 || !item.note.isNullOrBlank()
    val isPersonalPlan = item.origin == AgendaItemOrigin.PERSONAL_PLAN
    val isEditablePersonalPlan = isPersonalPlan && item.personalPlanId != null
    val cardShape = RoundedCornerShape(if (compact) 22.dp else 26.dp)
    var expanded by rememberSaveable(item.id) { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .shadow(
                elevation = if (item.isImportant) 22.dp else 14.dp,
                shape = cardShape,
                ambientColor = Color.Black.copy(alpha = 0.13f),
                spotColor = accent.copy(alpha = if (item.isImportant) 0.22f else 0.14f)
            )
            .animateContentSize()
            .then(
                if (hasExpandableContent || isEditablePersonalPlan) {
                    Modifier.pointerInput(item.id, hasExpandableContent, isEditablePersonalPlan) {
                        detectTapGestures(
                            onTap = {
                                if (hasExpandableContent) {
                                    expanded = !expanded
                                }
                            },
                            onLongPress = {
                                if (isEditablePersonalPlan) {
                                    onCardClick()
                                }
                            }
                        )
                    }
                } else {
                    Modifier
                }
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = when {
                isPersonalPlan -> colorScheme.surfaceContainerHigh.copy(alpha = 0.96f)
                item.isImportant -> colorScheme.surfaceBright
                else -> colorScheme.surface
            }
        ),
        border = BorderStroke(
            width = 1.dp,
            color = when {
                item.isImportant -> accent.copy(alpha = 0.22f)
                isPersonalPlan -> colorScheme.outlineVariant.copy(alpha = 0.55f)
                else -> colorScheme.outlineVariant.copy(alpha = 0.42f)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (item.isImportant) 9.dp else 5.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = if (isPersonalPlan) {
                        colorScheme.surfaceContainerHigh.copy(alpha = 0.35f)
                    } else {
                        Color.Transparent
                    }
                )
        ) {
            Icon(
                imageVector = agendaIcon(item.kind),
                contentDescription = null,
                tint = accent.copy(alpha = if (item.isImportant) 0.08f else 0.06f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 10.dp, end = 10.dp)
                    .size(if (compact) 54.dp else 72.dp)
                    .graphicsLayer {
                        rotationZ = -10f
                    }
            )

            Column(
                modifier = Modifier.padding(if (compact) 14.dp else 18.dp),
                verticalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 12.dp)
            ) {
                if (isPersonalPlan) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PersonalPlanHeader(accent = accent)
                        if (isEditablePersonalPlan) {
                            TextButton(onClick = onCardClick) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = accent
                                )
                                Spacer(Modifier.width(6.dp))
                                Text("Edit", color = accent)
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(if (compact) 10.dp else 14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    TimeRail(
                        accent = accent,
                        icon = agendaIcon(item.kind),
                        timeLabel = timeLabel(item),
                        durationLabel = durationLabel(item),
                        compact = compact
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 10.dp)
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AgendaBadge(label = item.badge, accent = accent)
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
                            if (isPersonalPlan) {
                                item.reminderMinutesBefore?.toReminderChipLabel()?.let { label ->
                                    AgendaSource(label = label)
                                }
                            }
                        }

                        Text(
                            text = item.title,
                            style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                            color = colorScheme.onSurface,
                            fontWeight = FontWeight.SemiBold
                        )

                        if (item.linkedStudentNames.isNotEmpty()) {
                            StudentStackRow(
                                names = item.linkedStudentNames,
                                accent = accent
                            )
                        }

                        Text(
                            text = item.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant,
                            maxLines = if (expanded) Int.MAX_VALUE else 2,
                            overflow = if (expanded) TextOverflow.Clip else TextOverflow.Ellipsis
                        )

                        AnimatedVisibility(
                            visible = expanded && !item.note.isNullOrBlank(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ) {
                                Text(
                                    text = item.note.orEmpty(),
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
                                    TextButton(onClick = onCTAClick) {
                                        Text(cta, color = accent)
                                    }
                                } ?: Spacer(Modifier.width(1.dp))

                                if (hasExpandableContent) {
                                    TextButton(onClick = { expanded = !expanded }) {
                                        Text(if (expanded) "Show less" else "Show more")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalPlanHeader(accent: Color) {
    Surface(
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

@Composable
private fun TimeRail(
    accent: Color,
    icon: ImageVector,
    timeLabel: String,
    durationLabel: String,
    compact: Boolean
) {
    Column(
        modifier = Modifier.width(if (compact) 78.dp else 88.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 40.dp else 48.dp)
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
}

@Composable
private fun StudentStackRow(
    names: List<String>,
    accent: Color
) {
    val visibleNames = names.take(3)
    val leadName = names.firstOrNull()?.substringBefore(" ").orEmpty()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(modifier = Modifier.width((visibleNames.size * 18 + 14).dp)) {
            visibleNames.forEachIndexed { index, name ->
                Surface(
                    modifier = Modifier.offset(x = (index * 18).dp),
                    shape = CircleShape,
                    color = accent.copy(alpha = 0.16f),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier.size(26.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = name.initials(),
                            style = MaterialTheme.typography.labelSmall,
                            color = accent,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        if (names.size == 1) {
            Text(
                text = leadName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        } else {
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.58f)
            ) {
                Text(
                    text = "$leadName +${names.size - 1}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
        color = if (important) {
            accent.copy(alpha = 0.18f)
        } else {
            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.72f)
        }
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

private fun timeLabel(item: AgendaItemUi): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    return "${item.start.format(formatter)} - ${item.end.format(formatter)}"
}

private fun durationLabel(item: AgendaItemUi): String {
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

private fun agendaIcon(kind: AgendaItemKind): ImageVector = when (kind) {
    AgendaItemKind.CLASS -> Icons.AutoMirrored.Filled.MenuBook
    AgendaItemKind.ASSESSMENT -> Icons.Default.CalendarMonth
    AgendaItemKind.MEETING -> Icons.Default.CalendarMonth
    AgendaItemKind.CALL -> Icons.Default.VideoCall
    AgendaItemKind.ANNOUNCEMENT -> Icons.Default.Campaign
    AgendaItemKind.PERSONAL -> Icons.Default.AutoStories
}

private fun Int.toReminderChipLabel(): String = when (this) {
    5 -> "Reminder 5 min before"
    15 -> "Reminder 15 min before"
    30 -> "Reminder 30 min before"
    60 -> "Reminder 1 hour before"
    else -> "Reminder $this min before"
}

private fun String.initials(): String =
    trim()
        .split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }
