package com.schoolbridge.v2.ui.home.common

import android.graphics.Color
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
/*
@Composable
fun SeverityChip(severity: AlertSeverity, color: Color) {
    val label = when (severity) {
        AlertSeverity.LOW -> "Low"
        AlertSeverity.MEDIUM -> "Medium"
        AlertSeverity.HIGH -> "High"
    }

    Text(
        text = label,
        color = color,
        style = MaterialTheme.typography.labelSmall,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    )
}*/