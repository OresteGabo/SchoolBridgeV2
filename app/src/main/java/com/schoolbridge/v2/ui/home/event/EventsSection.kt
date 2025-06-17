package com.schoolbridge.v2.ui.home.event

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.components.AppSubHeader
import com.schoolbridge.v2.ui.common.components.SpacerS
import com.schoolbridge.v2.ui.event.EventRepository

/**
 * Section for upcoming events.
 *
 * @param onViewAllEventsClick Callback for the "View All" button.
 * @param onEventClick Callback when an individual event card is clicked.
 * @param modifier Modifier applied to the section.
 */
@Composable
fun EventsSection(
    onViewAllEventsClick: () -> Unit,
    onEventClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val maxInitialEvents = 3 // Define how many events to show initially

    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300), label = "rotationAnimation"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(durationMillis = 300))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppSubHeader("📅 " + t( R.string.upcoming_events))
            TextButton(onClick = onViewAllEventsClick) {
                Text(text = t(R.string.view_all), style = MaterialTheme.typography.labelLarge)
            }
        }

        SpacerS()

        // Use the actual EventRepository to get events
        val eventRepository = remember { EventRepository() } // Or inject via Hilt/DI
        val events = remember { eventRepository.getUpcomingEvents() } // Get the list of Event objects

        if (events.isEmpty()) {
            Text(t(R.string.no_upcoming_events), style = MaterialTheme.typography.bodyMedium)
        } else {
            // Determine which events to show based on the expanded state
            val eventsToShow = if (expanded) {
                events
            } else {
                events.take(maxInitialEvents)
            }

            eventsToShow.forEachIndexed { index, event ->
                EventCardCompact(
                    event = event,
                    index = index,
                    onEventClick = onEventClick
                )
            }

            // Show "Show More" button only if there are more events to display
            if (events.size > maxInitialEvents) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = !expanded }
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (expanded) t(R.string.show_less) else t(R.string.show_more),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = Icons.Filled.ExpandMore,
                        contentDescription = if (expanded) t(R.string.show_less) else t(R.string.show_more),
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .rotate(rotationState)
                    )
                }
            }
        }
    }
}