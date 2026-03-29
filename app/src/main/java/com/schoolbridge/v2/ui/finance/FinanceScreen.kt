package com.schoolbridge.v2.ui.finance

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.schoolbridge.v2.R
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.components.CustomSideNavBar
import com.schoolbridge.v2.data.remote.FinanceApiServiceImpl
import com.schoolbridge.v2.data.repository.implementations.FinanceRepositoryImpl
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.AdaptivePageFrame
import com.schoolbridge.v2.ui.common.BackendConnectionState
import com.schoolbridge.v2.ui.common.BackendStatusTile
import com.schoolbridge.v2.ui.common.FriendlyNetworkErrorCard
import com.schoolbridge.v2.ui.common.SchoolBridgePatternBackground
import com.schoolbridge.v2.ui.common.isExpandedLayout
import com.schoolbridge.v2.ui.common.isWideLandscapeLayout
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val FINANCE_RETRY_INTERVAL_MILLIS = 5_000L
private const val FINANCE_RECOVERY_TILE_DURATION_MILLIS = 2_500L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceScreen(
    userSessionManager: UserSessionManager,
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpanded = isExpandedLayout()
    val useWideLandscapeNav = isWideLandscapeLayout()
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
    val latestUiState by rememberUpdatedState(uiState)
    val dashboard = uiState.dashboard
    val currentUserId = currentUser?.userId
    var showRecoveryTile by remember { mutableStateOf(false) }
    var hadConnectionIssue by remember { mutableStateOf(false) }
    val showTeacherDesk = remember(currentUser, dashboard, uiState.hasLoadedOnce) {
        val activeRole = currentUser?.currentRole
        val hasFinanceData = dashboard.transactions.isNotEmpty() || dashboard.outstandingItems.isNotEmpty()
        activeRole == UserRole.TEACHER &&
            currentUser?.isParent() != true &&
            currentUser?.isStudent() != true &&
            currentUser?.isAdmin() != true &&
            uiState.hasLoadedOnce &&
            !hasFinanceData
    }

    LaunchedEffect(currentUser?.userId) {
        currentUser?.userId?.let(financeViewModel::loadFinance)
    }

    LaunchedEffect(currentUserId, uiState.errorMessage) {
        val userId = currentUserId ?: return@LaunchedEffect
        if (uiState.errorMessage.isNullOrBlank()) return@LaunchedEffect

        while (isActive && !latestUiState.errorMessage.isNullOrBlank()) {
            delay(FINANCE_RETRY_INTERVAL_MILLIS)
            if (!latestUiState.isLoading) {
                financeViewModel.retry(userId)
            }
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (!uiState.errorMessage.isNullOrBlank()) {
            hadConnectionIssue = true
            showRecoveryTile = false
            return@LaunchedEffect
        }

        if (hadConnectionIssue && uiState.hasLoadedOnce) {
            showRecoveryTile = true
            hadConnectionIssue = false
            delay(FINANCE_RECOVERY_TILE_DURATION_MILLIS)
            showRecoveryTile = false
        }
    }

    var selectedStudentId by remember(dashboard) { mutableStateOf(dashboard.selectedStudentId) }
    var selectedFilter by remember { mutableStateOf(FinanceFilter.All) }
    var selectedSection by remember { mutableStateOf(FinanceSection.Overview) }
    var selectedCategory by remember { mutableStateOf<FinanceCategory?>(null) }
    var financeSearchQuery by remember { mutableStateOf("") }

    val visibleTransactions = remember(dashboard, selectedStudentId, selectedFilter, selectedCategory) {
        dashboard.transactions.filter { transaction ->
            val studentMatches = selectedStudentId == ALL_STUDENTS_ID || transaction.studentId == selectedStudentId
            val filterMatches = when (selectedFilter) {
                FinanceFilter.All -> true
                FinanceFilter.Outstanding -> transaction.status == FinanceStatus.Pending
                FinanceFilter.Chat -> transaction.source == FinanceSource.Chat
                FinanceFilter.Completed -> transaction.status == FinanceStatus.Completed
            }
            val categoryMatches = selectedCategory == null || transaction.category == selectedCategory
            studentMatches && filterMatches && categoryMatches
        }
    }
    val visibleOutstandingItems = remember(dashboard, selectedStudentId, financeSearchQuery) {
        val normalizedQuery = financeSearchQuery.trim().lowercase()
        dashboard.outstandingItemsFor(selectedStudentId).filter { item ->
            if (normalizedQuery.isBlank()) return@filter true
            listOf(
                item.title,
                item.description,
                item.studentName,
                item.category.label,
                item.amountLabel,
                item.dueDateLabel
            ).any { candidate ->
                candidate.lowercase().contains(normalizedQuery)
            }
        }
    }
    val searchedTransactions = remember(visibleTransactions, financeSearchQuery) {
        val normalizedQuery = financeSearchQuery.trim().lowercase()
        visibleTransactions.filter { transaction ->
            if (normalizedQuery.isBlank()) return@filter true
            listOf(
                transaction.title,
                transaction.description,
                transaction.studentName,
                transaction.category.label,
                transaction.reference,
                transaction.paymentMethod,
                transaction.amountLabel,
                transaction.dateLabel
            ).any { candidate ->
                candidate.lowercase().contains(normalizedQuery)
            }
        }
    }

    val selectedStudent = dashboard.students.firstOrNull { it.id == selectedStudentId }
    val hasUsableFinanceData = uiState.hasLoadedOnce
    val isReconnectAttemptInFlight = hadConnectionIssue && uiState.isLoading
    val showBlockingOutage =
        (!uiState.errorMessage.isNullOrBlank() || isReconnectAttemptInFlight) && !hasUsableFinanceData
    val showPinnedStatusTile =
        (!uiState.errorMessage.isNullOrBlank() || isReconnectAttemptInFlight || showRecoveryTile) &&
            hasUsableFinanceData

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            if (showTeacherDesk) t(R.string.teaching_desk_title)
                            else t(R.string.finance_title)
                        )
                        Text(
                            text = if (showTeacherDesk) {
                                t(R.string.teaching_desk_subtitle)
                            } else {
                                t(R.string.finance_subtitle)
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (!useWideLandscapeNav) {
                CustomBottomNavBar(
                    currentScreen = currentScreen,
                    onTabSelected = onTabSelected,
                    currentUser = currentUser
                )
            }
        },
        modifier = modifier
    ) { paddingValues ->
        val financeContent: @Composable () -> Unit = {
            AdaptivePageFrame(
                modifier = Modifier.fillMaxSize(),
                contentPadding = if (useWideLandscapeNav) PaddingValues(horizontal = 20.dp, vertical = 0.dp) else paddingValues,
                maxContentWidth = if (useWideLandscapeNav) 1680.dp else if (isExpanded) 1320.dp else 1240.dp
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    SchoolBridgePatternBackground(dotAlpha = 0.02f, gradientAlpha = 0.05f)
                    if (showBlockingOutage) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BackendStatusTile(
                                title = if (isReconnectAttemptInFlight) "Reconnecting to SchoolBridge" else "SchoolBridge is temporarily down",
                                message = uiState.errorMessage ?: "Finance is temporarily unavailable while we check the connection again.",
                                helperText = if (isReconnectAttemptInFlight) {
                                    "Trying again automatically every few seconds."
                                } else {
                                    "We will keep checking the server so finance data returns as soon as it is available."
                                },
                                state = if (isReconnectAttemptInFlight) BackendConnectionState.RECONNECTING else BackendConnectionState.DISCONNECTED,
                                onRetry = currentUserId?.let { userId -> { financeViewModel.retry(userId) } },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                        if (showTeacherDesk) {
                            item {
                                TeacherDeskPlaceholder()
                            }
                        } else {
                            if (selectedSection == FinanceSection.Overview && isExpanded) {
                                item {
                                    ExpandedOverviewSection(
                                        dashboard = dashboard,
                                        selectedStudent = selectedStudent,
                                        currentUserId = currentUserId,
                                        selectedFilter = selectedFilter,
                                        selectedStudentId = selectedStudentId,
                                        selectedCategory = selectedCategory,
                                        onPaySchoolFees = {
                                            selectedSection = FinanceSection.Due
                                            selectedFilter = FinanceFilter.Outstanding
                                        },
                                        onShareReceipt = {
                                            selectedSection = FinanceSection.Activity
                                            selectedFilter = FinanceFilter.Completed
                                            financeSearchQuery = ""
                                        },
                                        onRemindMe = {
                                            selectedSection = FinanceSection.Due
                                        },
                                        onFilterLedger = {
                                            selectedSection = FinanceSection.Activity
                                        }
                                    )
                                }
                            } else {
                                item {
                                    FinanceHeroCard(
                                        dashboard = dashboard,
                                        selectedStudent = selectedStudent,
                                        currentUserId = currentUserId,
                                        selectedFilter = selectedFilter
                                    )
                                }

                                if (selectedSection == FinanceSection.Overview) {
                                    item {
                                        OverviewStatsGrid(
                                            stats = dashboard.statsFor(selectedStudentId)
                                        )
                                    }

                                    item {
                                        FinanceCategoryBreakdownCard(
                                            dashboard = dashboard,
                                            selectedStudentId = selectedStudentId,
                                            selectedCategory = selectedCategory,
                                            onCategorySelected = { category ->
                                                selectedCategory = category
                                                selectedSection = FinanceSection.Activity
                                            }
                                        )
                                    }

                                    item {
                                        FinanceMonthlyTrendCard(
                                            dashboard = dashboard,
                                            selectedStudentId = selectedStudentId
                                        )
                                    }

                                    item {
                                        QuickActionsRow(
                                            onPaySchoolFees = {
                                                selectedSection = FinanceSection.Due
                                                selectedFilter = FinanceFilter.Outstanding
                                            },
                                            onShareReceipt = {
                                                selectedSection = FinanceSection.Activity
                                                selectedFilter = FinanceFilter.Completed
                                                financeSearchQuery = ""
                                            },
                                            onRemindMe = {
                                                selectedSection = FinanceSection.Due
                                            },
                                            onFilterLedger = {
                                                selectedSection = FinanceSection.Activity
                                            }
                                        )
                                    }
                                }
                            }

                            item {
                                StudentSelectorRow(
                                    students = dashboard.students,
                                    currentUserId = currentUserId,
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
                                        errorMessage = uiState.errorMessage,
                                        onRetry = currentUserId?.let { userId ->
                                            { financeViewModel.retry(userId) }
                                        }
                                    )
                                }
                            }

                            when (selectedSection) {
                                FinanceSection.Overview -> {
                                    item {
                                        SectionTitle(
                                            title = "Urgent items",
                                            subtitle = "The most time-sensitive balances stay close to the summary instead of getting buried in the ledger."
                                        )
                                    }

                                    items(
                                        items = dashboard.outstandingItemsFor(selectedStudentId).take(if (isExpanded) 4 else 2),
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
                                        FinanceSearchField(
                                            query = financeSearchQuery,
                                            onQueryChange = { financeSearchQuery = it },
                                            placeholder = "Search charges, students, categories..."
                                        )
                                    }

                                    item {
                                        SectionTitle(
                                            title = "Outstanding items",
                                            subtitle = "Parents should be able to settle fees, fines, and uniforms without digging through conversations."
                                        )
                                    }

                                    items(
                                        items = visibleOutstandingItems,
                                        key = { it.id }
                                    ) { item ->
                                        OutstandingChargeCard(item = item)
                                    }

                                    if (visibleOutstandingItems.isEmpty()) {
                                        item {
                                            FinanceEmptyState("No outstanding charges match the current student and search.")
                                        }
                                    }
                                }

                                FinanceSection.Activity -> {
                                    item {
                                        FinanceSearchField(
                                            query = financeSearchQuery,
                                            onQueryChange = { financeSearchQuery = it },
                                            placeholder = "Search transactions, references, payment methods..."
                                        )
                                    }

                                    item {
                                        FinanceFilterRow(
                                            selectedFilter = selectedFilter,
                                            selectedCategory = selectedCategory,
                                            onFilterSelected = { selectedFilter = it },
                                            onCategorySelected = { selectedCategory = it }
                                        )
                                    }

                                    item {
                                        SectionTitle(
                                            title = "Transaction timeline",
                                            subtitle = "Every payment, whether started in chat, finance, or by an admin, lands here."
                                        )
                                    }

                                    items(
                                        items = searchedTransactions,
                                        key = { it.id }
                                    ) { transaction ->
                                        TransactionCard(transaction = transaction)
                                    }

                                    if (searchedTransactions.isEmpty()) {
                                        item {
                                            FinanceEmptyState("No transactions match the current student and filter yet.")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (showPinnedStatusTile) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .align(Alignment.TopCenter)
                    ) {
                        BackendStatusTile(
                            title = when {
                                showRecoveryTile -> "SchoolBridge is back"
                                isReconnectAttemptInFlight -> "Reconnecting to SchoolBridge"
                                else -> "SchoolBridge is temporarily down"
                            },
                            message = when {
                                !uiState.errorMessage.isNullOrBlank() -> uiState.errorMessage ?: "Finance is temporarily unavailable."
                                isReconnectAttemptInFlight -> "We are checking the server again so the latest finance values can come back."
                                else -> "Finance values are live again and the dashboard has reconnected successfully."
                            },
                            helperText = when {
                                showRecoveryTile -> "Fresh finance data is available again."
                                isReconnectAttemptInFlight -> "Trying again automatically every few seconds."
                                else -> "Current figures may be stale until SchoolBridge comes back."
                            },
                            state = when {
                                showRecoveryTile -> BackendConnectionState.RECOVERED
                                isReconnectAttemptInFlight -> BackendConnectionState.RECONNECTING
                                else -> BackendConnectionState.DISCONNECTED
                            },
                            onRetry = currentUserId?.let { userId -> { financeViewModel.retry(userId) } },
                            modifier = Modifier.alpha(0.98f)
                        )
                        }
                    }
                }
            }
        }

        if (useWideLandscapeNav) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CustomSideNavBar(
                    currentScreen = currentScreen,
                    onTabSelected = onTabSelected,
                    currentUser = currentUser
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                ) {
                    financeContent()
                }
            }
        } else {
            financeContent()
        }
    }
}

@Composable
private fun ExpandedOverviewSection(
    dashboard: FinanceDashboard,
    selectedStudent: FinanceStudent?,
    currentUserId: String?,
    selectedFilter: FinanceFilter,
    selectedStudentId: String,
    selectedCategory: FinanceCategory?,
    onPaySchoolFees: () -> Unit,
    onShareReceipt: () -> Unit,
    onRemindMe: () -> Unit,
    onFilterLedger: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1.4f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FinanceHeroCard(
                dashboard = dashboard,
                selectedStudent = selectedStudent,
                currentUserId = currentUserId,
                selectedFilter = selectedFilter
            )
            OverviewStatsGrid(
                stats = dashboard.statsFor(selectedStudentId)
            )
            FinanceCategoryBreakdownCard(
                dashboard = dashboard,
                selectedStudentId = selectedStudentId,
                selectedCategory = selectedCategory,
                onCategorySelected = {}
            )
            FinanceMonthlyTrendCard(
                dashboard = dashboard,
                selectedStudentId = selectedStudentId
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionsRow(
                onPaySchoolFees = onPaySchoolFees,
                onShareReceipt = onShareReceipt,
                onRemindMe = onRemindMe,
                onFilterLedger = onFilterLedger
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Large-screen summary",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Tablet layouts work better when the summary and actions stay visible beside the ledger instead of stacking into one tall phone column.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun TeacherDeskPlaceholder() {
    val colors = MaterialTheme.colorScheme
    val quickActions = listOf(
        t(R.string.teacher_action_attendance),
        t(R.string.teacher_action_grades),
        t(R.string.teacher_action_message_parents),
        t(R.string.teacher_action_upload_quiz)
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            colors = CardDefaults.cardColors(containerColor = colors.secondaryContainer),
            shape = RoundedCornerShape(28.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = t(R.string.teacher_desk_empty_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.onSecondaryContainer
                )
                Text(
                    text = t(R.string.teacher_desk_empty_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.onSecondaryContainer.copy(alpha = 0.86f)
                )
            }
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = t(R.string.teacher_desk_actions_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    quickActions.forEach { action ->
                        AssistChip(
                            onClick = {},
                            label = { Text(action) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        )
                    }
                }
                Text(
                    text = t(R.string.teacher_desk_footer_note),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FinanceFeedbackCard(
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: (() -> Unit)? = null
) {
    when {
        isLoading -> {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Text(
                    text = "Loading finance records from the school server.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        !errorMessage.isNullOrBlank() -> {
            FriendlyNetworkErrorCard(
                rawMessage = errorMessage,
                onRetry = onRetry
            )
        }
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
private fun FinanceCategoryBreakdownCard(
    dashboard: FinanceDashboard,
    selectedStudentId: String,
    selectedCategory: FinanceCategory?,
    onCategorySelected: (FinanceCategory) -> Unit
) {
    val breakdown = remember(dashboard, selectedStudentId) {
        dashboard.categoryBreakdown(selectedStudentId).take(4)
    }
    if (breakdown.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Money flow by category",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "This graph compares what is already paid with what is still waiting, so the balance pressure is visible before you open every finance row.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                val maxTotal = breakdown.maxOfOrNull { it.totalAmount }?.takeIf { it > 0.0 } ?: 1.0
                breakdown.forEach { item ->
                    FinanceCategoryGraphRow(
                        item = item,
                        maxTotal = maxTotal,
                        isSelected = selectedCategory == item.category,
                        onClick = { onCategorySelected(item.category) }
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FinanceGraphLegendChip(
                    label = "Paid",
                    color = MaterialTheme.colorScheme.tertiary
                )
                FinanceGraphLegendChip(
                    label = "Outstanding",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun FinanceCategoryGraphRow(
    item: FinanceCategoryBreakdown,
    maxTotal: Double,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val trackColor = MaterialTheme.colorScheme.surfaceContainerHighest
    val paidColor = MaterialTheme.colorScheme.tertiary
    val outstandingColor = MaterialTheme.colorScheme.primary
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .background(
                if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.32f)
                else Color.Transparent
            )
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(
                        imageVector = item.category.icon,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(16.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = item.category.label,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${item.paidAmount.asMoney()} paid • ${item.outstandingAmount.asMoney()} waiting",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Text(
                text = item.totalAmount.asMoney(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .size(height = 14.dp, width = 0.dp)
        ) {
            val radius = CornerRadius(size.height / 2f, size.height / 2f)
            drawRoundRect(
                color = trackColor,
                cornerRadius = radius
            )

            val totalWidth = ((item.totalAmount / maxTotal).toFloat() * size.width).coerceAtLeast(0f)
            if (totalWidth <= 0f) return@Canvas

            val paidWidth = ((item.paidAmount / maxTotal).toFloat() * size.width)
                .coerceIn(0f, totalWidth)
            val outstandingWidth = (totalWidth - paidWidth).coerceAtLeast(0f)

            if (paidWidth > 0f) {
                drawRoundRect(
                    color = paidColor,
                    size = Size(width = paidWidth, height = size.height),
                    cornerRadius = radius
                )
            }
            if (outstandingWidth > 0f) {
                drawRoundRect(
                    color = outstandingColor,
                    topLeft = Offset(x = paidWidth, y = 0f),
                    size = Size(width = outstandingWidth, height = size.height),
                    cornerRadius = radius
                )
            }
        }
    }
}

@Composable
private fun FinanceGraphLegendChip(
    label: String,
    color: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun FinanceMonthlyTrendCard(
    dashboard: FinanceDashboard,
    selectedStudentId: String
) {
    val trend = remember(dashboard, selectedStudentId) {
        dashboard.monthlyTrend(selectedStudentId)
    }
    if (trend.size < 2) return

    val maxAmount = trend.maxOfOrNull { maxOf(it.paidAmount, it.outstandingAmount) }
        ?.takeIf { it > 0.0 } ?: 1.0
    val axisColor = MaterialTheme.colorScheme.outlineVariant
    val guideColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.35f)
    val paidColor = MaterialTheme.colorScheme.tertiary
    val outstandingColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Monthly balance rhythm",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "This timeline shows how payments and upcoming pressure are moving across recent months.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .size(height = 180.dp, width = 0.dp)
            ) {
                val leftPadding = 20.dp.toPx()
                val rightPadding = 8.dp.toPx()
                val topPadding = 12.dp.toPx()
                val bottomPadding = 28.dp.toPx()
                val graphWidth = size.width - leftPadding - rightPadding
                val graphHeight = size.height - topPadding - bottomPadding
                val stepX = if (trend.size > 1) graphWidth / (trend.size - 1) else 0f

                drawLine(
                    color = axisColor,
                    start = Offset(leftPadding, topPadding + graphHeight),
                    end = Offset(leftPadding + graphWidth, topPadding + graphHeight),
                    strokeWidth = 1.dp.toPx()
                )

                fun pointY(amount: Double): Float {
                    val normalized = (amount / maxAmount).toFloat().coerceIn(0f, 1f)
                    return topPadding + graphHeight - (graphHeight * normalized)
                }

                for (index in trend.indices) {
                    val x = leftPadding + (stepX * index)
                    drawLine(
                        color = guideColor,
                        start = Offset(x, topPadding),
                        end = Offset(x, topPadding + graphHeight),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                for (index in 0 until trend.lastIndex) {
                    val start = trend[index]
                    val end = trend[index + 1]
                    val startX = leftPadding + (stepX * index)
                    val endX = leftPadding + (stepX * (index + 1))
                    drawLine(
                        color = paidColor,
                        start = Offset(startX, pointY(start.paidAmount)),
                        end = Offset(endX, pointY(end.paidAmount)),
                        strokeWidth = 3.dp.toPx()
                    )
                    drawLine(
                        color = outstandingColor,
                        start = Offset(startX, pointY(start.outstandingAmount)),
                        end = Offset(endX, pointY(end.outstandingAmount)),
                        strokeWidth = 3.dp.toPx()
                    )
                }

                trend.forEachIndexed { index, point ->
                    val x = leftPadding + (stepX * index)
                    drawCircle(
                        color = paidColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, pointY(point.paidAmount))
                    )
                    drawCircle(
                        color = outstandingColor,
                        radius = 4.dp.toPx(),
                        center = Offset(x, pointY(point.outstandingAmount))
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                trend.forEach { point ->
                    Text(
                        text = point.month.month.name.take(3),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FinanceGraphLegendChip(
                    label = "Paid trend",
                    color = MaterialTheme.colorScheme.tertiary
                )
                FinanceGraphLegendChip(
                    label = "Outstanding trend",
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
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
    currentUserId: String?,
    selectedFilter: FinanceFilter
) {
    val colorScheme = MaterialTheme.colorScheme
    val title = when {
        selectedStudent?.id == currentUserId -> "Your money flow"
        selectedStudent != null -> "${selectedStudent.name}'s money flow"
        else -> "Family finance overview"
    }
    val subtitle = when (selectedFilter) {
        FinanceFilter.All -> "Payments from MoMo, chat requests, fines, uniforms, and tuition stay in one ledger."
        FinanceFilter.Outstanding -> "Pending items stay visible until they are paid or resolved."
        FinanceFilter.Chat -> "Chat-origin transactions are tracked here so no one has to reopen old conversations."
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
    currentUserId: String?,
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
                val label = when {
                    student.id == ALL_STUDENTS_ID -> student.name
                    student.id == currentUserId -> "${student.name} (You)"
                    else -> student.name
                }
                FilterChip(
                    selected = selectedStudentId == student.id,
                    onClick = { onSelected(student.id) },
                    label = { Text(label) },
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
private fun QuickActionsRow(
    onPaySchoolFees: () -> Unit,
    onShareReceipt: () -> Unit,
    onRemindMe: () -> Unit,
    onFilterLedger: () -> Unit
) {
    val actions = listOf(
        QuickActionUi("Pay school fees", Icons.Default.Payments, FinanceTone.Positive) to onPaySchoolFees,
        QuickActionUi("Share receipt", Icons.AutoMirrored.Filled.ReceiptLong, FinanceTone.Info) to onShareReceipt,
        QuickActionUi("Remind me", Icons.Default.NotificationsActive, FinanceTone.Warning) to onRemindMe,
        QuickActionUi("Filter ledger", Icons.Default.FilterList, FinanceTone.Accent) to onFilterLedger
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
            actions.forEach { (action, onClick) ->
                val actionTint = financeToneColor(action.tone)
                Card(
                    onClick = onClick,
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
private fun FinanceSearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null
            )
        },
        placeholder = { Text(placeholder) },
        shape = RoundedCornerShape(22.dp)
    )
}

@Composable
private fun FinanceFilterRow(
    selectedFilter: FinanceFilter,
    selectedCategory: FinanceCategory?,
    onFilterSelected: (FinanceFilter) -> Unit,
    onCategorySelected: (FinanceCategory?) -> Unit
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { onCategorySelected(null) },
                label = { Text("All categories") }
            )
            FinanceCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.label) }
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
                PaymentMethodBadge(transaction = transaction)
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
private fun PaymentMethodBadge(transaction: FinanceTransactionUi) {
    val brand = financePaymentBrandFor(transaction)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            brand?.let {
                Image(
                    painter = painterResource(id = it.logoRes),
                    contentDescription = it.label,
                    modifier = Modifier
                        .size(width = 26.dp, height = 18.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Fit
                )
            }
            Text(
                text = brand?.label ?: transaction.paymentMethod,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
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

private data class FinancePaymentBrand(
    val label: String,
    val logoRes: Int
)

private fun financePaymentBrandFor(transaction: FinanceTransactionUi): FinancePaymentBrand? {
    val method = transaction.paymentMethod.lowercase()
    val reference = transaction.reference.lowercase()

    return when {
        "momo" in method || "mtn" in method -> FinancePaymentBrand("MoMo", R.drawable.momo)
        "airtel" in method -> FinancePaymentBrand("Airtel", R.drawable.airtel_logo)
        "equity" in method || reference.startsWith("eq-") -> FinancePaymentBrand("Equity", R.drawable.equity_bank)
        method == "bank transfer" || reference.startsWith("bk-") -> FinancePaymentBrand("BK", R.drawable.bk_logo)
        "bk" in method -> FinancePaymentBrand("BK", R.drawable.bk_logo)
        "irembo" in method || "irembo" in reference -> FinancePaymentBrand("Irembo", R.drawable.irembo_logo)
        else -> null
    }
}
