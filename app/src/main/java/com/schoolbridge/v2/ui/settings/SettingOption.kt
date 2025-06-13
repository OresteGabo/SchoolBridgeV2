package com.schoolbridge.v2.ui.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.ui.graphics.vector.ImageVector

sealed class SettingOption(
    val title: String,
    val icon: ImageVector
) {
    object Profile : SettingOption("Profile", Icons.Default.Person)
    object Notifications : SettingOption("Notifications", Icons.Default.Notifications)
    object Language : SettingOption("Language", Icons.Default.Language)
    object Theme : SettingOption("Theme", Icons.Default.Palette)
    object ViewLinkRequests : SettingOption("View Link Requests", Icons.Default.Link)
    object HelpFAQ : SettingOption("Help & FAQ", Icons.AutoMirrored.Filled.Help)
    object About : SettingOption("About", Icons.Default.Info)
    object DataPrivacy : SettingOption("DataPrivacy", Icons.Default.Shield)
    object Logout : SettingOption("Logout", Icons.AutoMirrored.Filled.ExitToApp)

    companion object {
        val all = listOf(
            Profile,
            Notifications,
            Language,
            Theme,
            ViewLinkRequests,
            HelpFAQ,
            About,
            DataPrivacy,
            Logout
        )
    }
}