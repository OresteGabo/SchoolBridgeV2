package com.schoolbridge.v2.ui.message

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.android.awaitFrame
import com.schoolbridge.v2.R
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.components.CustomSideNavBar
import com.schoolbridge.v2.data.session.UserSessionManager
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.AdaptivePageFrame
import com.schoolbridge.v2.ui.common.isWideLandscapeLayout
import com.schoolbridge.v2.ui.navigation.MainAppScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageScreen(
    userSessionManager: UserSessionManager,
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    onBack: () -> Unit,
    onMessageConversationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    val currentUser by userSessionManager.currentUser.collectAsState(initial = null)
    val useWideLandscapeNav = isWideLandscapeLayout()
    val searchFocusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    var isSearchVisible by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var showFilterMenu by remember { mutableStateOf(false) }
    var selectedFilter by rememberSaveable { mutableStateOf(MessageInboxFilter.ALL) }
    var inboxPrefsRestored by remember(currentUser?.userId) { mutableStateOf(false) }

    LaunchedEffect(currentUser?.userId) {
        val restored = MessageInboxPreferences.getInboxUiState(context, currentUser?.userId)
        isSearchVisible = restored.isSearchVisible
        searchQuery = restored.searchQuery
        selectedFilter = restored.selectedFilter
        inboxPrefsRestored = true
    }

    LaunchedEffect(isSearchVisible, searchQuery, selectedFilter, inboxPrefsRestored, currentUser?.userId) {
        if (!inboxPrefsRestored) return@LaunchedEffect
        MessageInboxPreferences.saveInboxUiState(
            context = context,
            userId = currentUser?.userId,
            state = MessageInboxUiPrefs(
                isSearchVisible = isSearchVisible,
                searchQuery = searchQuery,
                selectedFilter = selectedFilter
            )
        )
    }

    LaunchedEffect(isSearchVisible) {
        if (isSearchVisible) {
            awaitFrame()
            searchFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column {
                LargeTopAppBar(
                    title = {
                        Column {
                            Text(
                                text  = t(R.string.messages_title),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text  = t(R.string.messages_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            val nextVisible = !isSearchVisible
                            isSearchVisible = nextVisible
                            if (!nextVisible) {
                                searchQuery = ""
                                keyboardController?.hide()
                            }
                        }) {
                            Icon(
                                imageVector = if (isSearchVisible) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = if (isSearchVisible) "Close search" else "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Box {
                            IconButton(onClick = { showFilterMenu = true }) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Filter",
                                    tint = if (selectedFilter == MessageInboxFilter.ALL) {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    } else {
                                        MaterialTheme.colorScheme.primary
                                    }
                                )
                            }
                            DropdownMenu(
                                expanded = showFilterMenu,
                                onDismissRequest = { showFilterMenu = false }
                            ) {
                                MessageInboxFilter.entries.forEach { filter ->
                                    DropdownMenuItem(
                                        text = { Text(filter.label) },
                                        leadingIcon = {
                                            if (filter == selectedFilter) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null
                                                )
                                            }
                                        },
                                        onClick = {
                                            selectedFilter = filter
                                            showFilterMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    colors = TopAppBarDefaults.largeTopAppBarColors(
                        containerColor        = MaterialTheme.colorScheme.surface,
                        scrolledContainerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor     = MaterialTheme.colorScheme.onSurface
                    )
                )
                if (isSearchVisible) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp)
                            .focusRequester(searchFocusRequester),
                        singleLine = true,
                        placeholder = {
                            Text("Search people, topics, or message text")
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null
                            )
                        },
                        shape = MaterialTheme.shapes.extraLarge
                    )
                }
                if (selectedFilter != MessageInboxFilter.ALL) {
                    AssistChip(
                        onClick = { selectedFilter = MessageInboxFilter.ALL },
                        label = { Text(selectedFilter.label) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    )
                }
            }
        },
        bottomBar = {
            if (!useWideLandscapeNav) {
                CustomBottomNavBar(
                    currentScreen = currentScreen,
                    onTabSelected = onTabSelected,
                    currentUser = currentUser
                )
            }
        }
    ) { innerPadding ->
        if (useWideLandscapeNav) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                CustomSideNavBar(
                    currentScreen = currentScreen,
                    onTabSelected = onTabSelected,
                    currentUser = currentUser
                )
                AdaptivePageFrame(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                    maxContentWidth = 1680.dp
                ) {
                    MessageConversationScreen(
                        userSessionManager = userSessionManager,
                        initialConversationId = null,
                        searchQuery = searchQuery,
                        inboxFilter = selectedFilter,
                        onConversationSelected = onMessageConversationClick
                    )
                }
            }
        } else {
            AdaptivePageFrame(
                modifier = Modifier.fillMaxSize(),
                contentPadding = innerPadding
            ) {
                MessageConversationScreen(
                    userSessionManager = userSessionManager,
                    initialConversationId  = null,
                    searchQuery = searchQuery,
                    inboxFilter = selectedFilter,
                    onConversationSelected = onMessageConversationClick
                )
            }
        }
    }
}
