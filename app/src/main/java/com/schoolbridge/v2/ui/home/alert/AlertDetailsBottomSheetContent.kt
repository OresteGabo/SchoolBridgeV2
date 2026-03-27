package com.schoolbridge.v2.ui.home.alert

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.schoolbridge.v2.data.remote.MessageApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.AlertRepositoryImpl
import com.schoolbridge.v2.data.repository.implementations.MessagingRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.R
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.components.InfoRowWithIcon
import com.schoolbridge.v2.ui.home.common.NewBadge
import com.schoolbridge.v2.ui.home.common.SeverityChip
import java.time.format.DateTimeFormatter

/* ------------ MAIN BOTTOM‑SHEET CONTENT ------------ */
@Composable
fun AlertDetailsBottomSheetContent(
    alertId: String,
    userSessionManager: UserSessionManager,
) {
    val viewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModelFactory(
            AlertRepositoryImpl(
                messagingRepository = MessagingRepositoryImpl(MessageApiServiceImpl(userSessionManager)),
                userSessionManager = userSessionManager
            )
        )
    )
    val uiState by viewModel.uiState.collectAsState()
    val alerts = uiState.alerts
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

            SeverityChip(
                alert.severity,
            )
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
                InfoRowWithIcon(Icons.Default.AccountBalance, t(R.string.alerts_source_label),
                    alert.sourceOrganization ?: "—")
                InfoRowWithIcon(Icons.Default.School, t(R.string.alerts_student_label),
                    alert.studentName ?: t(R.string.alerts_not_linked_student))
                InfoRowWithIcon(Icons.Default.Notifications, t(R.string.alerts_type_label),
                    alert.type.name.replaceFirstChar { it.uppercase() })
                InfoRowWithIcon(Icons.Default.People, t(R.string.alerts_publisher_type_label),
                    alert.publisherType.name.lowercase()
                        .replaceFirstChar { it.uppercase() })
            }
        }

    }
}
