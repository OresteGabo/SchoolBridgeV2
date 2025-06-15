package com.schoolbridge.v2.ui.settings

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.ui.settings.components.SettingItem
import androidx.compose.foundation.layout.*
import kotlinx.coroutines.delay

@Preview
@Composable
private fun SettingsScreenPrev() {
    SettingsScreen(
        onLogout = {},
        onBack = {},
        onViewLinkRequests = {},
        onNavigateToProfile = {},
        onNavigateToNotifications = {},
        onNavigateToHelp = {},
        onNavigateToAbout = {},
        onDataPrivacy = {},
        isDarkTheme = TODO(),
        onToggleTheme = TODO(),
    )
}







@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onViewLinkRequests: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToHelp: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onDataPrivacy: () -> Unit,
    isDarkTheme: Boolean,                   // Pass in current theme state
    onToggleTheme: (Boolean) -> Unit       // Callback to toggle theme
) {
    val settingsItems = SettingOption.all.filterNot { it is SettingOption.Logout }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showThemeDialog by remember { mutableStateOf(false) }

    // For animation
    var animateContent by remember { mutableStateOf(false) }
    val animatedItemStates = remember { mutableStateOf(List(settingsItems.size) { false }) }

    LaunchedEffect(Unit) {
        animateContent = true
        settingsItems.forEachIndexed { index, _ ->
            delay(80)
            animatedItemStates.value = animatedItemStates.value.toMutableList().also { it[index] = true }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            AnimatedVisibility(
                visible = animateContent,
                //enter = fadeIn(animationSpec = tween(300)) + slideInVertically(initialOffsetY = { it / 10 }, tween(300)),
                modifier = Modifier.weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 16.dp)
                ) {
                    items(settingsItems.size) { index ->
                        val option = settingsItems[index]
                        val itemAnimated = animatedItemStates.value[index]

                        AnimatedVisibility(
                            visible = itemAnimated,
                           // enter = fadeIn(tween(300)) + slideInVertically(initialOffsetY = { it / 2 }, tween(300)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SettingItem(
                                option = option,
                                onClick = {
                                    when (option) {
                                        is SettingOption.Profile -> onNavigateToProfile()
                                        is SettingOption.Notifications -> onNavigateToNotifications()
                                        is SettingOption.Language -> showLanguageDialog = true
                                        is SettingOption.Theme -> showThemeDialog = true // or handle toggle directly
                                        is SettingOption.ViewLinkRequests -> onViewLinkRequests()
                                        is SettingOption.HelpFAQ -> onNavigateToHelp()
                                        is SettingOption.About -> onNavigateToAbout()
                                        is SettingOption.DataPrivacy -> onDataPrivacy()
                                        else -> {}
                                    }
                                },
                                currentLanguage = "en",          // or your current language state
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { newValue ->
                                    onToggleTheme(newValue)
                                }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = animateContent,
                //enter = fadeIn(tween(300, 400)) + slideInVertically(initialOffsetY = { it / 5 }, tween(300, 400)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onLogout,
                    modifier = Modifier.padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Logout")
                }
            }
        }

        // Language Dialog (Your existing dialog code goes here, unchanged)
        if (showLanguageDialog) {
            // ... your existing language selection dialog ...
        }

        // Theme Toggle Dialog
        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Select Theme") },
                text = {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTheme(false); showThemeDialog = false }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(selected = !isDarkTheme, onClick = { onToggleTheme(false); showThemeDialog = false })
                            Spacer(Modifier.width(8.dp))
                            Text("Light")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTheme(true); showThemeDialog = false }
                                .padding(vertical = 8.dp)
                        ) {
                            RadioButton(selected = isDarkTheme, onClick = { onToggleTheme(true); showThemeDialog = false })
                            Spacer(Modifier.width(8.dp))
                            Text("Dark")
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showThemeDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
