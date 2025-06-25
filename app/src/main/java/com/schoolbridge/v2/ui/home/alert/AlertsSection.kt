package com.schoolbridge.v2.ui.home.alert

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertsViewModel
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.components.AppSubHeader
import com.schoolbridge.v2.ui.components.SpacerS

/**
 * Section for recent alerts.
 *
 * @param onViewAllAlertsClick Callback for the "View All" button.
 * @param modifier Modifier applied to the section.
 */
@Composable
fun AlertsSection(
    viewModel: AlertsViewModel = viewModel(),
    onViewAllAlertsClick: () -> Unit,
    onAlertClick: (Alert) -> Unit,  // New callback
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val maxInitialAlerts = 3
    val alerts by viewModel.alerts.collectAsState()
    val rotationState by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        animationSpec = tween(durationMillis = 300),
        label = "rotationAnimation"
    )
    val alertsToShow = if (expanded) alerts else alerts.take(maxInitialAlerts)

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
            AppSubHeader("💬 " + t(R.string.recent_alerts))
            TextButton(onClick = onViewAllAlertsClick) {
                Text(text = t(R.string.view_all), style = MaterialTheme.typography.labelLarge)
            }
        }

        SpacerS()

        alertsToShow.forEachIndexed { index, alert ->
            AlertCardCompact(
                alert = alert,
                index = index,
                onClick = {
                    viewModel.markAsRead(alert.id)
                    // Wait until recomposition happens
                    val updatedAlert = viewModel.alerts.value.find { it.id == alert.id } ?: alert
                    onAlertClick(updatedAlert)
                }
            )
        }

        if (alerts.size > maxInitialAlerts) {
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
                    contentDescription = if (expanded) "Show less alerts" else "Show more alerts",
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .rotate(rotationState)
                )
            }
        }
    }
}