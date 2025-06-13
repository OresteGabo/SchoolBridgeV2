package com.schoolbridge.v2.ui.settings

import android.util.Log
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

@Preview
@Composable
private fun SettingsScreenPrev() {
    SettingsScreen(
        onLogout = {},
        onBack = {},
        onViewLinkRequests = {},
        onNavigateToProfile ={},
        onNavigateToNotifications = {},
        onNavigateToHelp={},
        onNavigateToAbout = {},
        onDataPrivacy = {},

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

    ) {
    val settingsItems = SettingOption.all.filterNot { it is SettingOption.Logout }

    var showDialog by remember { mutableStateOf(false) }

    val languageNames = mapOf(
        "en" to "English",
        "fr" to "Français",
        "rw" to "Kinyarwanda",
        "sw" to "Swahili (Coming soon)"
    )

    /*var currentLanguage by remember {
        "en" //mutableStateOf(SessionManager.currentLocale)
    }*/
    val onChange: (String) -> Unit = { newLanguage ->
        //currentLanguage = newLanguage
        //SessionManager.currentLocale= newLanguage
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn() {
                items(settingsItems.size) { optionIndex ->
                    val option = settingsItems[optionIndex]
                    Log.d("Settings", "Rendering: ${option.title}") // <-- Add this

                    SettingItem(
                        option = option,
                        onClick = {
                            when (option) {
                                is SettingOption.Profile -> onNavigateToProfile()
                                is SettingOption.Notifications -> onNavigateToNotifications()
                                is SettingOption.Language -> { showDialog = true }
                                is SettingOption.ViewLinkRequests -> onViewLinkRequests()
                                is SettingOption.HelpFAQ -> onNavigateToHelp()
                                is SettingOption.About -> onNavigateToAbout()
                                is SettingOption.DataPrivacy -> onDataPrivacy()
                                else -> {}
                            }
                        },
                        currentLanguage = "en" //languageNames.getOrDefault(currentLanguage, currentLanguage),
                    )
                }

            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onLogout,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text("Logout")
            }

            if (showDialog) {
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text("Select Language") },
                    text = {
                        Column {
                            languageNames.forEach { (code, name) ->
                                val enabled = code != "sw"
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .alpha(if (enabled) 1f else 0.5f)
                                        .clickable(enabled) {
                                            onChange(code)
                                            showDialog = false
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = "en" == code,
                                        onClick = if (enabled) {
                                            { onChange(code); showDialog = false }
                                        } else null,
                                        enabled = enabled
                                    )
                                    Text(name)
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "Note: The app may follow your device's system language by default.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }
}