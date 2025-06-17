package com.schoolbridge.v2.ui.home.alert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.domain.messaging.AlertsViewModel
import com.schoolbridge.v2.ui.home.InfoRowWithIcon
import com.schoolbridge.v2.ui.home.SeverityChip
import java.time.format.DateTimeFormatter
import com.schoolbridge.v2.ui.home.common.NewBadge

/* ------------ MAIN BOTTOM‑SHEET CONTENT ------------ */
@Composable
fun AlertDetailsBottomSheetContent(
    alertId: String,
    viewModel: AlertsViewModel = viewModel(),
) {
    val alerts by viewModel.alerts.collectAsState()
    val alert = alerts.find { it.id == alertId } ?: return

    val dateText = remember(alert.timestamp) {
        alert.timestamp.format(DateTimeFormatter.ofPattern("HH:mm, MMM d • yyyy"))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 20.dp)
            .verticalScroll(rememberScrollState())
            // ✨ makes every size‑change (incl. our button) animate smoothly
            .animateContentSize(tween(300))
    ) {

        /* ---------- HEADER ---------- */
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.AccountCircle, contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.width(8.dp))

            Column(Modifier.weight(1f)) {
                Text(
                    text = alert.publisherName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = dateText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            SeverityChip(alert.severity)
        }

        Spacer(Modifier.height(16.dp))

        /* ---------- TITLE ---------- */
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = alert.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (!alert.isRead) {
                Spacer(Modifier.width(8.dp))
                NewBadge()         // small helper (see below)
            }
        }

        Spacer(Modifier.height(10.dp))

        /* ---------- MESSAGE BODY ---------- */
        Text(
            text = alert.message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(Modifier.height(24.dp))

        /* ---------- INFO CARD ---------- */
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.elevatedCardColors(MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(Modifier.padding(16.dp)) {
                InfoRowWithIcon(Icons.Default.AccountBalance, "Source",
                    alert.sourceOrganization ?: "—")
                InfoRowWithIcon(Icons.Default.School, "Student",
                    alert.studentName ?: "Not linked to a student")
                InfoRowWithIcon(Icons.Default.Notifications, "Type",
                    alert.type.name.replaceFirstChar { it.uppercase() })
                InfoRowWithIcon(Icons.Default.People, "Publisher Type",
                    alert.publisherType.name.lowercase()
                        .replaceFirstChar { it.uppercase() })
            }
        }

        /* ---------- MARK‑AS‑UNREAD BUTTON (animated) ---------- */
        AnimatedVisibility(
            visible = alert.isRead,               // ⬅ reacts to live state
            enter = fadeIn() + expandVertically(),
            exit  = fadeOut() + shrinkVertically()
        ) {
            Column {                        // keep a Spacer with the button so both animate
                Spacer(Modifier.height(20.dp))
                OutlinedButton(
                    onClick = { viewModel.markAsUnread(alert.id) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(Icons.Default.MarkEmailUnread, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Mark as Unread")
                }
            }
        }
    }
}