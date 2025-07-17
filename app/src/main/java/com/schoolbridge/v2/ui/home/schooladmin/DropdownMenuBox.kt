package com.schoolbridge.v2.ui.home.schooladmin

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.schoolbridge.v2.domain.academic.TimetableEntryType

@Composable
fun DropdownMenuBox(current: TimetableEntryType, onChange: (TimetableEntryType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }) {
            Text(current.name.lowercase().replaceFirstChar { it.uppercase() })
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TimetableEntryType.values().forEach {
                DropdownMenuItem(
                    text = { Text(it.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    onClick = {
                        onChange(it)
                        expanded = false
                    }
                )
            }
        }
    }
}
