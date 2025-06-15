package com.schoolbridge.v2.ui.settings.components

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.schoolbridge.v2.ui.settings.SettingOption

@Composable
fun SettingItem(
    option: SettingOption,
    onClick: () -> Unit,
    currentLanguage: String,
    isDarkTheme: Boolean,                  // Add theme state
    onThemeToggle: (Boolean) -> Unit       // Callback to toggle theme
) {
    val linkRequestStatus = if (option is SettingOption.ViewLinkRequests) "pending" else null

    ListItem(
        headlineContent = {
            Text(option.title, style = MaterialTheme.typography.bodyLarge)
        },
        leadingContent = {
            Icon(
                imageVector = option.icon,
                contentDescription = option.title
            )
        },
        trailingContent = {
            when (option) {
                is SettingOption.Theme -> {
                    Switch(
                        checked = isDarkTheme,
                        onCheckedChange = { onThemeToggle(it) }
                    )
                }

                is SettingOption.Language -> {
                    Text(currentLanguage.uppercase(), style = MaterialTheme.typography.bodyMedium)
                }

                is SettingOption.ViewLinkRequests -> {
                    when (linkRequestStatus) {
                        "pending" -> Icon(Icons.Default.HourglassEmpty, contentDescription = "Pending")
                        "validated" -> Icon(Icons.Default.CheckCircle, contentDescription = "Validated")
                        else -> Icon(Icons.Default.Link, contentDescription = "Link Requests")
                    }
                }

                else -> {}
            }
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
