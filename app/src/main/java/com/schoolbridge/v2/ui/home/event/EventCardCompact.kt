package com.schoolbridge.v2.ui.home.event

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.event.Event
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Duration


/**
 * Compact card for a single event.
 *
 * @param event The [Event] object to display.
 * @param index Used for staggered animation delay.
 * @param onEventClick Callback when the card is clicked, passes the eventId.
 * @param modifier Modifier applied to the card.
 */
@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EventCardCompact(
    event: Event,
    index: Int,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    val accentColor = if (event.isMandatory) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.primary
    }



    // Helper to compute human-readable time remaining
    fun getTimeRemaining(): String {
        val now = LocalDateTime.now()
        return when {
            event.startTime.isBefore(now) && event.endTime.isAfter(now) -> "Ongoing"
            event.startTime.isBefore(now) -> "Started"
            else -> {
                val duration = Duration.between(now, event.startTime)
                val days = duration.toDays()
                when (days) {
                    0L -> "Today"
                    1L -> "In 1 day"
                    in 2..6 -> "In $days days"
                    in 7..13 -> "Next week"
                    else -> "In ${days / 7} weeks"
                }
            }
        }
    }

    val timeRemainingText = getTimeRemaining()
    val timeRemainingColor = when (timeRemainingText) {
        "Ongoing", "Soon", "Today" -> MaterialTheme.colorScheme.error // Or a vibrant accent
        else -> MaterialTheme.colorScheme.tertiary
    }
    val timeRemainingStyle = when (timeRemainingText) {
        "Ongoing", "Soon" -> MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.ExtraBold) // Slightly larger, bolder
        else -> MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(tween(1000)) + slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(1000, delayMillis = index * 100)
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
            onClick = {
                event.isRead = true
                onEventClick(event.id)
            }
        ) {
            Row(modifier = Modifier.fillMaxSize()) {

                // Vertical accent bar
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            accentColor,
                            shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = 12.dp, horizontal = 0.dp)
                ) {
                    // Title row with optional lock icon and NEW badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (event.isMandatory) {
                            Icon(
                                imageVector = Icons.Filled.Lock,
                                contentDescription = "Mandatory event",
                                tint = accentColor,
                                modifier = Modifier
                                    .size(20.dp)
                                    .padding(end = 6.dp)
                            )
                        }
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (!event.isRead) {
                            Text(
                                text = "NEW",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Organizer row (second row)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = "Organizer",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = event.organizer,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                    }



                    Spacer(modifier = Modifier.height(8.dp))

                    Row(modifier= Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween){
                        // Badge for event type
                        Box(
                            modifier = Modifier
                                .background(
                                    accentColor.copy(alpha = 0.12f),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (event.isMandatory) Icons.Filled.Lock else Icons.Filled.Event,
                                    contentDescription = if (event.isMandatory) "Mandatory" else "Optional",
                                    tint = accentColor,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (event.isMandatory) "MANDATORY" else "OPTIONAL",
                                    color = accentColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Time remaining row
                        Surface(
                            modifier = Modifier.padding(4.dp),
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f), // A subtle background
                            shape = RoundedCornerShape(8.dp) // Rounded corners for the badge effect
                        ) {
                            Text(
                                text = getTimeRemaining(),
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp) // Add padding inside the surface
                            )
                        }
                    }
                }
            }
        }
    }
}

fun formatDuration(duration: Duration): String {
    val days = duration.toDays()
    val hours = duration.toHours() % 24
    return when {
        days > 1 -> "$days days"
        days == 1L -> "1 day"
        hours > 1 -> "$hours hrs"
        hours == 1L -> "1 hr"
        else -> "less than 1 hr"
    }
}