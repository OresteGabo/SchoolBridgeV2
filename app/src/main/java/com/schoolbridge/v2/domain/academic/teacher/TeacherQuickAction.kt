package com.schoolbridge.v2.domain.academic.teacher

import androidx.compose.ui.graphics.vector.ImageVector

/* ─────────────────────────────────────────────────────────────── */
/* 1. Data model for a quick action                               */
/* ─────────────────────────────────────────────────────────────── */
data class TeacherQuickAction(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit = {}
)