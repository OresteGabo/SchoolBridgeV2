package com.schoolbridge.v2.ui.settings.about

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Construction

val aboutReleaseNotes = listOf(
    VersionInfo(
        title = "V2: School workflow",
        meaning = "The platform now feels less like a simple school portal and more like a guided operations space for communication, approvals, finance, and planning.",
        releaseDate = "March 2026",
        focus = "Threads, role requests, finance visibility, schedule planning, and realtime updates",
        future = "Structured replies, media workflows, and deeper school-side moderation tools",
        icon = Icons.Default.Forum,
        isCurrent = true
    ),
    VersionInfo(
        title = "V1: Umusingi",
        meaning = "Umusingi means foundation, the solid base for identity, onboarding, and the first connected school flows.",
        releaseDate = "May 2025",
        focus = "Core infrastructure, identity, and onboarding",
        future = "Grow into a real school coordination platform",
        icon = Icons.Default.Construction,
        isCurrent = false
    ),
    VersionInfo(
        title = "Next: Guided responses",
        meaning = "Communication becomes even more intentional with reply permissions, expected answer types, and richer media or document workflows inside threads.",
        releaseDate = "Planned",
        focus = "Response controls, uploads, invited calls, and school moderation",
        future = "Institutional communication that feels natural without becoming generic chat",
        icon = Icons.Default.AutoAwesome,
        isCurrent = false
    )
)
