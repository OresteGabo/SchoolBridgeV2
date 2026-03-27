package com.schoolbridge.v2.ui.home.alert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.schoolbridge.v2.data.remote.MessageApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.AlertRepositoryImpl
import com.schoolbridge.v2.data.repository.implementations.MessagingRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.messaging.Alert
import com.schoolbridge.v2.domain.messaging.AlertSeverity
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.AdaptivePageFrame
import com.schoolbridge.v2.ui.common.FriendlyNetworkErrorCard
import com.schoolbridge.v2.ui.common.SchoolBridgePatternBackground
import com.schoolbridge.v2.ui.common.isExpandedLayout
import com.schoolbridge.v2.ui.home.common.SeverityChip
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertsScreen(
    userSessionManager: UserSessionManager,
    onBack: (() -> Unit)? = null,
    bottomBar: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val viewModel: AlertsViewModel = viewModel(
        factory = AlertsViewModelFactory(
            AlertRepositoryImpl(
                messagingRepository = MessagingRepositoryImpl(MessageApiServiceImpl(userSessionManager)),
                userSessionManager = userSessionManager
            )
        )
    )
    val isExpanded = isExpandedLayout()
    val uiState by viewModel.uiState.collectAsState()
    val alerts = uiState.alerts
    val hasUnreadAlerts = remember(alerts) { alerts.any { !it.isRead } }

    var searchQuery by remember { mutableStateOf("") }
    var selectedAlert by remember { mutableStateOf<Alert?>(null) }

    val filteredAlerts = remember(searchQuery, alerts) {
        if (searchQuery.isBlank()) alerts
        else alerts.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
                it.message.contains(searchQuery, ignoreCase = true) ||
                it.source.contains(searchQuery, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(t(R.string.alerts_label)) },
                actions = {
                    if (hasUnreadAlerts) {
                        TextButton(onClick = viewModel::markAllAsRead) {
                            Text(t(R.string.mark_all_read))
                        }
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        bottomBar = {
            bottomBar?.invoke()
        },
        modifier = modifier.fillMaxSize()
    ) { paddingValues ->
        AdaptivePageFrame(
            modifier = Modifier.fillMaxSize(),
            contentPadding = paddingValues,
            maxContentWidth = if (isExpanded) 1200.dp else 1240.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                SchoolBridgePatternBackground(dotAlpha = 0.016f, gradientAlpha = 0.035f)
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        placeholder = { Text(t(R.string.alerts_search_placeholder)) },
                        singleLine = true,
                        keyboardActions = KeyboardActions(onSearch = { }),
                        colors = TextFieldDefaults.colors()
                    )

                    SwipeRefresh(
                        state = rememberSwipeRefreshState(uiState.isLoading),
                        onRefresh = {
                            viewModel.refresh()
                        },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        if (uiState.errorMessage != null && alerts.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                FriendlyNetworkErrorCard(
                                    rawMessage = uiState.errorMessage,
                                    onRetry = viewModel::refresh
                                )
                            }
                        } else if (filteredAlerts.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    t(R.string.alerts_empty_search),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredAlerts.size) { alertIndex ->
                                    AlertCardDetailed(
                                        alert = filteredAlerts[alertIndex],
                                        index = alertIndex,
                                        onClick = { alert ->
                                            viewModel.markAsRead(alert.id)
                                            selectedAlert = alert
                                        },
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        selectedAlert?.let { alert ->
            ModalBottomSheet(
                onDismissRequest = { selectedAlert = null }
            ) {
                AlertDetailsBottomSheetContent(
                    alertId = alert.id,
                    userSessionManager = userSessionManager
                )
            }
        }
    }
}



/**
 * A more detailed alert card that shows title, message, timestamp, severity, etc.
 */
@Composable
fun AlertCardDetailed(alert: Alert,
                      index: Int,
                      onClick: (Alert) -> Unit,
                      modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { visible = true }

    val accentColor = when (alert.severity) {
        AlertSeverity.HIGH -> MaterialTheme.colorScheme.error
        AlertSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiary
        AlertSeverity.LOW -> MaterialTheme.colorScheme.secondary
    }.copy(alpha = if (alert.isRead) 0.4f else 1f)

    val textColor = if (alert.isRead)
        MaterialTheme.colorScheme.onSurfaceVariant
    else
        MaterialTheme.colorScheme.onSecondaryContainer

    val timestamp = remember(alert.timestamp) {
        DateTimeFormatter.ofPattern("MMM d, h:mm a").format(alert.timestamp)
    }

    val severityColor = when (alert.severity) {
        AlertSeverity.HIGH -> MaterialTheme.colorScheme.error
        AlertSeverity.MEDIUM -> MaterialTheme.colorScheme.tertiary
        AlertSeverity.LOW -> MaterialTheme.colorScheme.secondary
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
                .padding(vertical = 4.dp)
                .clickable { onClick(alert) },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .background(
                            accentColor,
                            RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                        )
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "Alert",
                            tint = textColor,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 6.dp)
                        )

                        Text(
                            text = alert.title,
                            style = MaterialTheme.typography.labelLarge,
                            color = textColor,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(Modifier.width(4.dp))

                        Text(
                            text = timestamp,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = alert.message,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        SeverityChip(severity = alert.severity)

                        if (!alert.isRead) {
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = "NEW",
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
