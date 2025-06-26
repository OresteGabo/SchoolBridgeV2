package com.schoolbridge.v2.ui.settings

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.ui.settings.components.SettingItem
import com.schoolbridge.v2.data.preferences.AppPreferences
import com.schoolbridge.v2.ui.theme.AppPalette
import com.schoolbridge.v2.ui.theme.Contrast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        isDarkTheme = false,
        onToggleTheme = {},
        currentPalette = TODO(),
        currentContrast = TODO(),
        onPalettePicked = TODO(),
        onContrastPicked = TODO()
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
    isDarkTheme: Boolean,
    currentPalette: AppPalette,
    currentContrast: Contrast,
    onToggleTheme: (Boolean) -> Unit,
    onPalettePicked: (AppPalette) -> Unit,
    onContrastPicked: (Contrast) -> Unit
) {
    val settingsItems = SettingOption.all.filterNot { it is SettingOption.Logout }
    var showThemeDialog by remember { mutableStateOf(false) }
    var animateContent by remember { mutableStateOf(false) }
    val animatedItemStates = remember { mutableStateOf(List(settingsItems.size) { false }) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val languageNames = mapOf(
        "en" to "English",
        "fr" to "Français",
        "rw" to "Kinyarwanda",
        "sw" to "Swahili (Coming soon)"
    )

    val storedLanguage by AppPreferences.getLanguage(context).collectAsState(initial = "en")
    var currentLanguage by remember(storedLanguage) { mutableStateOf(storedLanguage) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    val onChangeLanguage: (String) -> Unit = { newLang ->
        if (newLang != storedLanguage && newLang != "sw") {
            coroutineScope.launch {
                AppPreferences.setLanguage(context, newLang)
            }
        }
    }

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
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SettingItem(
                                option = option,
                                onClick = {
                                    when (option) {
                                        is SettingOption.Profile -> onNavigateToProfile()
                                        is SettingOption.Notifications -> onNavigateToNotifications()
                                        is SettingOption.Language -> showLanguageDialog = true
                                        is SettingOption.Theme -> showThemeDialog = true
                                        is SettingOption.ViewLinkRequests -> onViewLinkRequests()
                                        is SettingOption.HelpFAQ -> onNavigateToHelp()
                                        is SettingOption.About -> onNavigateToAbout()
                                        is SettingOption.DataPrivacy -> onDataPrivacy()
                                        else -> {}
                                    }
                                },
                                currentLanguage = storedLanguage,
                                isDarkTheme = isDarkTheme,
                                onThemeToggle = { newValue -> onToggleTheme(newValue) }
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = animateContent,
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

        if (showLanguageDialog) {
            AlertDialog(
                onDismissRequest = { showLanguageDialog = false },
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
                                        onChangeLanguage(code)
                                        showLanguageDialog = false
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = storedLanguage == code,
                                    onClick = if (enabled) {
                                        { onChangeLanguage(code); showLanguageDialog = false }
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
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showLanguageDialog = false }) {
                        Text("Close")
                    }
                }
            )
        }

        if (showThemeDialog) {
            AlertDialog(
                onDismissRequest = { showThemeDialog = false },
                title = { Text("Customize Theme") },
                text = {
                    Column {
                        Text("Color Palette", style = MaterialTheme.typography.titleSmall)
                        AppPalette.entries.forEach { palette ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onPalettePicked(palette)
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                RadioButton(
                                    selected = palette == currentPalette,
                                    onClick = { onPalettePicked(palette) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(palette.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Contrast Level", style = MaterialTheme.typography.titleSmall)
                        Contrast.entries.forEach { contrast ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        onContrastPicked(contrast)
                                    }
                                    .padding(vertical = 6.dp)
                            ) {
                                RadioButton(
                                    selected = contrast == currentContrast,
                                    onClick = { onContrastPicked(contrast) }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(contrast.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("Theme Mode", style = MaterialTheme.typography.titleSmall)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTheme(false) }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(selected = !isDarkTheme, onClick = { onToggleTheme(false) })
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Light")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onToggleTheme(true) }
                                .padding(vertical = 6.dp)
                        ) {
                            RadioButton(selected = isDarkTheme, onClick = { onToggleTheme(true) })
                            Spacer(modifier = Modifier.width(8.dp))
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

