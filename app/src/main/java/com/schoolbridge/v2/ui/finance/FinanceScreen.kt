package com.schoolbridge.v2.ui.finance

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.remote.FinanceApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.FinanceRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.ui.navigation.MainAppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    userSessionManager: UserSessionManager,
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by userSessionManager.currentUser.collectAsStateWithLifecycle(initialValue = null)
    val financeViewModel: FinanceViewModel = viewModel(
        factory = remember(userSessionManager) {
            FinanceViewModelFactory(
                financeRepository = FinanceRepositoryImpl(FinanceApiServiceImpl(userSessionManager)),
                userSessionManager = userSessionManager
            )
        }
    )
    val uiState by financeViewModel.uiState.collectAsStateWithLifecycle()
    val dashboard = uiState.dashboard

    LaunchedEffect(currentUser?.userId) {
        currentUser?.userId?.let(financeViewModel::loadFinance)
    }

    var selectedStudentId by remember(dashboard) { mutableStateOf(dashboard.selectedStudentId) }
    var selectedFilter by remember { mutableStateOf(FinanceFilter.All) }
    var selectedSection by remember { mutableStateOf(FinanceSection.Overview) }

    val visibleTransactions = remember(dashboard, selectedStudentId, selectedFilter) {
        dashboard.transactions.filter { transaction ->
            val studentMatches = selectedStudentId == ALL_STUDENTS_ID || transaction.studentId == selectedStudentId
            val filterMatches = when (selectedFilter) {
                FinanceFilter.All -> true
                FinanceFilter.Outstanding -> transaction.status == FinanceStatus.Pending
                FinanceFilter.Chat -> transaction.source == FinanceSource.Chat
                FinanceFilter.Completed -> transaction.status == FinanceStatus.Completed
            }
            studentMatches && filterMatches
        }
    }

    val selectedStudent = dashboard.students.firstOrNull { it.id == selectedStudentId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Finance")
                        Text(
                            text = "All school payments in one place",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        bottomBar = {
            CustomBottomNavBar(
                currentScreen = currentScreen,
                onTabSelected = onTabSelected
            )
        },
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                FinanceHeroCard(
                    dashboard = dashboard,
                    selectedStudent = selectedStudent,
                    selectedFilter = selectedFilter
                )
            }

            item {
                StudentSelectorRow(
                    students = dashboard.students,
                    selectedStudentId = selectedStudentId,
                    onSelected = { selectedStudentId = it }
                )
            }

            item {
                FinanceSectionTabs(
                    selectedSection = selectedSection,
                    onSectionSelected = { selectedSection = it }
                )
            }

            if (uiState.isLoading || uiState.errorMessage != null) {
                item {
                    FinanceFeedbackCard(
                        isLoading = uiState.isLoading,
                        errorMessage = uiState.errorMessage
                    )
                }
            }

            when (selectedSection) {
                FinanceSection.Overview -> {
                    item {
                        OverviewStatsGrid(
                            stats = dashboard.statsFor(selectedStudentId)
                        )
                    }

                    item {
                        QuickActionsRow()
                    }

                    item {
                        SectionTitle(
                            title = "Urgent items",
                            subtitle = "The most time-sensitive balances stay close to the summary instead of getting buried in the ledger."
                        )
                    }

                    items(
                        items = dashboard.outstandingItemsFor(selectedStudentId).take(2),
                        key = { it.id }
                    ) { item ->
                        OutstandingChargeCard(item = item)
                    }

                    if (dashboard.outstandingItemsFor(selectedStudentId).isEmpty()) {
                        item {
                            FinanceEmptyState("No urgent balances yet. As soon as the backend has due items for this family, they will show up here.")
                        }
                    }
                }

                FinanceSection.Due -> {
                    item {
                        SectionTitle(
                            title = "Outstanding items",
                            subtitle = "Parents should be able to settle fees, fines, and uniforms without digging through threads."
                        )
                    }

                    items(
                        items = dashboard.outstandingItemsFor(selectedStudentId),
                        key = { it.id }
                    ) { item ->
                        OutstandingChargeCard(item = item)
                    }

                    if (dashboard.outstandingItemsFor(selectedStudentId).isEmpty()) {
                        item {
                            FinanceEmptyState("No outstanding charges are currently linked to this student selection.")
                        }
                    }
                }

                FinanceSection.Activity -> {
                    item {
                        FinanceFilterRow(
                            selectedFilter = selectedFilter,
                            onFilterSelected = { selectedFilter = it }
                        )
                    }

                    item {
                        SectionTitle(
                            title = "Transaction timeline",
                            subtitle = "Every payment, whether started in chat, finance, or by an admin, lands here."
                        )
                    }

                    items(
                        items = visibleTransactions,
                        key = { it.id }
                    ) { transaction ->
                        TransactionCard(transaction = transaction)
                    }

                    if (visibleTransactions.isEmpty()) {
                        item {
                            FinanceEmptyState("No transactions match the current student and filter yet.")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceFeedbackCard(
    isLoading: Boolean,
    errorMessage: String?
) {
    val message = when {
        isLoading -> "Loading finance records from the backend."
        !errorMessage.isNullOrBlank() -> errorMessage
        else -> return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FinanceEmptyState(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FinanceSectionTabs(
    selectedSection: FinanceSection,
    onSectionSelected: (FinanceSection) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(
            title = "Finance spaces",
            subtitle = "Tabs keep the summary, unpaid items, and full activity separated so the screen breathes more."
        )
        TabRow(selectedTabIndex = selectedSection.ordinal) {
            FinanceSection.entries.forEach { section ->
                Tab(
                    selected = selectedSection == section,
                    onClick = { onSectionSelected(section) },
                    text = {
                        Text(section.label)
                    }
                )
            }
        }
    }
}

@Composable
private fun FinanceHeroCard(
    dashboard: FinanceDashboard,
    selectedStudent: FinanceStudent?,
    selectedFilter: FinanceFilter
) {
    val colorScheme = MaterialTheme.colorScheme
    val title = when {
        selectedStudent != null -> "${selectedStudent.name}'s money flow"
        else -> "Family finance overview"
    }
    val subtitle = when (selectedFilter) {
        FinanceFilter.All -> "Payments from MoMo, chat requests, fines, uniforms, and tuition stay in one ledger."
        FinanceFilter.Outstanding -> "Pending items stay visible until they are paid or resolved."
        FinanceFilter.Chat -> "Chat-origin transactions are tracked here so no one has to reopen old threads."
        FinanceFilter.Completed -> "Completed transactions are kept ready for receipts and reconciliation."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            colorScheme.surfaceContainerHighest,
                            colorScheme.surface,
                            colorScheme.primary.copy(alpha = 0.12f)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colorScheme.primary.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Unified finance tracker",
                            style = MaterialTheme.typography.labelLarge,
                            color = colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HeroMetric(
                        modifier = Modifier.weight(1f),
                        label = "Pending now",
                        value = dashboard.pendingAmountLabel(selectedStudent?.id ?: ALL_STUDENTS_ID),
                        icon = Icons.Default.WarningAmber,
                        tint = financeToneColor(FinanceTone.Warning)
                    )
                    HeroMetric(
                        modifier = Modifier.weight(1f),
                        label = "Paid this month",
                        value = dashboard.paidThisMonthLabel(selectedStudent?.id ?: ALL_STUDENTS_ID),
                        icon = Icons.Default.CheckCircle,
                        tint = financeToneColor(FinanceTone.Positive)
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    tint: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = tint.copy(alpha = 0.10f)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun StudentSelectorRow(
    students: List<FinanceStudent>,
    selectedStudentId: String,
    onSelected: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(
            title = "Track by student",
            subtitle = "Parents can switch children quickly while admins still see a combined view."
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            students.forEach { student ->
                FilterChip(
                    selected = selectedStudentId == student.id,
                    onClick = { onSelected(student.id) },
                    label = { Text(student.name) },
                    leadingIcon = {
                        if (selectedStudentId == student.id) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun OverviewStatsGrid(stats: List<FinanceStat>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(
            title = "Overview",
            subtitle = "A quick read of what is due, what is done, and what needs attention next."
        )
        stats.chunked(2).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                row.forEach { stat ->
                    StatCard(
                        stat = stat,
                        modifier = Modifier.weight(1f)
                    )
                }
                if (row.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    stat: FinanceStat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = financeToneColor(stat.tone).copy(alpha = 0.12f)
            ) {
                Icon(
                    imageVector = stat.icon,
                    contentDescription = null,
                    tint = financeToneColor(stat.tone),
                    modifier = Modifier
                        .padding(10.dp)
                        .size(18.dp)
                )
            }
            Text(
                text = stat.value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stat.note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsRow() {
    val actions = listOf(
        QuickActionUi("Pay school fees", Icons.Default.Payments, FinanceTone.Positive),
        QuickActionUi("Share receipt", Icons.AutoMirrored.Filled.ReceiptLong, FinanceTone.Info),
        QuickActionUi("Remind me", Icons.Default.NotificationsActive, FinanceTone.Warning),
        QuickActionUi("Filter ledger", Icons.Default.FilterList, FinanceTone.Accent)
    )

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(
            title = "Quick actions",
            subtitle = "These are the most common things a parent or school office will want from this tab."
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            actions.forEach { action ->
                val actionTint = financeToneColor(action.tone)
                Card(
                    modifier = Modifier.size(width = 156.dp, height = 122.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = actionTint.copy(alpha = 0.10f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ) {
                            Icon(
                                imageVector = action.icon,
                                contentDescription = null,
                                tint = actionTint,
                                modifier = Modifier
                                    .padding(10.dp)
                                    .size(18.dp)
                            )
                        }
                        Text(
                            text = action.label,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FinanceFilterRow(
    selectedFilter: FinanceFilter,
    onFilterSelected: (FinanceFilter) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SectionTitle(
            title = "Smart filters",
            subtitle = "This is where users stop hopping across chats just to rebuild payment history."
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FinanceFilter.values().forEach { filter ->
                FilterChip(
                    selected = selectedFilter == filter,
                    onClick = { onFilterSelected(filter) },
                    label = { Text(filter.label) }
                )
            }
        }
    }
}

@Composable
private fun OutstandingChargeCard(item: OutstandingChargeUi) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${item.studentName} • Due ${item.dueDateLabel}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                AmountPill(
                    text = item.amountLabel,
                    tint = financeToneColor(if (item.isUrgent) FinanceTone.Error else FinanceTone.Warning)
                )
            }

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SourceChip(label = item.category.label, tint = MaterialTheme.colorScheme.secondary)
                if (item.fromChat) {
                    SourceChip(label = "Requested in chat", tint = financeToneColor(FinanceTone.Info))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Pay now")
                }
                AssistChip(
                    onClick = {},
                    label = { Text("View details") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                            contentDescription = null
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun TransactionCard(transaction: FinanceTransactionUi) {
    val isPositive = transaction.status == FinanceStatus.Completed
    val accent = when (transaction.status) {
        FinanceStatus.Completed -> financeToneColor(FinanceTone.Positive)
        FinanceStatus.Pending -> financeToneColor(FinanceTone.Warning)
        FinanceStatus.Failed -> financeToneColor(FinanceTone.Error)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = accent.copy(alpha = 0.12f)
                    ) {
                        Icon(
                            imageVector = transaction.category.icon,
                            contentDescription = null,
                            tint = accent,
                            modifier = Modifier
                                .padding(12.dp)
                                .size(18.dp)
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = transaction.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${transaction.studentName} • ${transaction.dateLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = transaction.amountLabel,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPositive) accent else MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = transaction.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SourceChip(label = transaction.status.label, tint = accent)
                    SourceChip(label = transaction.category.label, tint = MaterialTheme.colorScheme.secondary)
                    SourceChip(label = transaction.source.label, tint = financeSourceColor(transaction.source))
                }
                Text(
                    text = transaction.paymentMethod,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (transaction.reference.isNotBlank()) {
                Text(
                    text = "Reference: ${transaction.reference}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (transaction.source == FinanceSource.Chat) {
                AssistChip(
                    onClick = {},
                    label = { Text("Open related conversation") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = null
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = financeSourceColor(transaction.source).copy(alpha = 0.10f),
                        labelColor = financeSourceColor(transaction.source),
                        leadingIconContentColor = financeSourceColor(transaction.source)
                    )
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(
    title: String,
    subtitle: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun AmountPill(
    text: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = tint.copy(alpha = 0.12f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = tint,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SourceChip(
    label: String,
    tint: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = tint.copy(alpha = 0.10f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun financeToneColor(tone: FinanceTone): Color {
    val colorScheme = MaterialTheme.colorScheme
    return when (tone) {
        FinanceTone.Positive -> colorScheme.primary
        FinanceTone.Warning -> colorScheme.tertiary
        FinanceTone.Info -> colorScheme.secondary
        FinanceTone.Accent -> colorScheme.inversePrimary
        FinanceTone.Error -> colorScheme.error
    }
}

@Composable
private fun financeSourceColor(source: FinanceSource): Color = when (source) {
    FinanceSource.Chat -> financeToneColor(FinanceTone.Info)
    FinanceSource.Finance -> financeToneColor(FinanceTone.Positive)
    FinanceSource.Admin -> financeToneColor(FinanceTone.Accent)
}
