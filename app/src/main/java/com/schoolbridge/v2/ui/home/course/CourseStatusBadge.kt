package com.schoolbridge.v2.ui.home.course

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.academic.CourseStatus

@Composable
fun CourseStatusBadge(status: CourseStatus) {
    val (label, color, icon) = when (status) {
        CourseStatus.VALIDATED -> Triple("VALIDATED", Color(0xFF4CAF50), Icons.Default.CheckCircle)
        CourseStatus.NOT_VALIDATED -> Triple("FAILED", Color(0xFFFF5722), Icons.Default.Warning)
        CourseStatus.RETAKE_REQUIRED -> Triple("RETAKE", Color(0xFFF44336), Icons.Default.Warning)
        CourseStatus.IN_PROGRESS -> Triple("IN PROGRESS", MaterialTheme.colorScheme.primary, Icons.Default.Notifications)
        CourseStatus.AWAITING_RESULTS -> Triple("PENDING", Color(0xFF03A9F4), Icons.Default.Info)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = color,
            style = MaterialTheme.typography.labelSmall
        )
        Spacer(modifier = Modifier.width(6.dp))
        Icon(
            imageVector = icon,
            contentDescription = "Course Status",
            tint = color,
            modifier = Modifier.size(16.dp)
        )
    }
}