package com.schoolbridge.v2.ui.home.common

import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.schoolbridge.v2.domain.messaging.AlertSeverity

/* ------------ SEVERITY CHIP ------------ */
@Composable
fun SeverityChip(severity: AlertSeverity) {
    val (bg, fg, text) = when (severity) {
        AlertSeverity.HIGH   -> Triple(MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "High • requires immediate attention")
        AlertSeverity.MEDIUM -> Triple(MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Medium • please review")
        AlertSeverity.LOW    -> Triple(MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Low • informational")
    }

    AssistChip(
        onClick = { /* no‑op */ },
        label    = { Text(text, maxLines = 1) },
        colors   = AssistChipDefaults.assistChipColors(
            containerColor = bg,
            labelColor     = fg
        ),
        elevation = AssistChipDefaults.assistChipElevation()
    )
}