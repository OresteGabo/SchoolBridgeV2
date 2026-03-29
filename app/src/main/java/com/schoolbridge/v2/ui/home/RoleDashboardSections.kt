package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.ui.components.AppSubHeader

private data class DashboardStat(
    val label: String,
    val value: String,
    val note: String,
    val icon: ImageVector,
    val tint: Color
)

private data class DashboardInsight(
    val title: String,
    val body: String,
    val meta: String,
    val icon: ImageVector,
    val tint: Color
)

private data class AdminRequestInboxItem(
    val id: String,
    val title: String,
    val requesterLabel: String,
    val conversationHint: String,
    val statusLabel: String,
    val icon: ImageVector,
    val tint: Color,
    val note: String
)

@Composable
fun StudentOverviewSection(
    currentUser: CurrentUser?,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val stats = remember {
        listOf(
            DashboardStat("Term Average", "74%", "Up 6% from last term", Icons.Default.TrendingUp, scheme.primary),
            DashboardStat("Pending Assessments", "3", "Physics, History, English", Icons.Default.PendingActions, scheme.tertiary),
            DashboardStat("Discipline Score", "18/20", "Good standing this term", Icons.Default.Verified, Color(0xFF1E8E5A)),
            DashboardStat("Fee Status", "12,000 RWF", "Outstanding this month", Icons.Default.AccountBalanceWallet, Color(0xFFC77700))
        )
    }
    val insights = remember {
        listOf(
            DashboardInsight(
                title = "Performance Pulse",
                body = "Mathematics and Biology are strong. English needs attention before the next assessment window.",
                meta = "Based on recent marks",
                icon = Icons.Default.TrendingUp,
                tint = scheme.primary
            ),
            DashboardInsight(
                title = "What To Do Next",
                body = "Revise English, check the next lab slot, and clear the pending fee before Friday.",
                meta = "Priority actions",
                icon = Icons.Default.AssignmentTurnedIn,
                tint = scheme.secondary
            )
        )
    }

    DashboardSection(
        title = "Student Snapshot",
        subtitle = currentUser?.let { "${it.firstName} ${it.lastName}" } ?: "Student overview",
        stats = stats,
        insights = insights,
        modifier = modifier
    )
}

@Composable
fun ParentOverviewSection(
    currentUser: CurrentUser?,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val childCount = currentUser?.linkedStudents?.size ?: 0
    val stats = remember(childCount) {
        listOf(
            DashboardStat("Linked Students", childCount.toString(), "Children under your watch", Icons.Default.Groups, scheme.primary),
            DashboardStat("Unread Messages", "5", "2 from teachers, 3 from school", Icons.AutoMirrored.Filled.Chat, scheme.secondary),
            DashboardStat("Fee Reminder", "18,500 RWF", "Due within 4 days", Icons.Default.Payments, Color(0xFFC77700)),
            DashboardStat("Attendance Flags", "2", "Late arrivals this week", Icons.Default.WarningAmber, scheme.error)
        )
    }
    val insights = remember {
        listOf(
            DashboardInsight(
                title = "Family Attention Needed",
                body = "One child has a low English score trend and another has two attendance flags this week.",
                meta = "Guardian alert",
                icon = Icons.Default.Rule,
                tint = scheme.error
            ),
            DashboardInsight(
                title = "Best Parent Actions",
                body = "Check the newest teacher messages, settle pending fees, and review tomorrow's schedule with your child.",
                meta = "Suggested today",
                icon = Icons.Default.Campaign,
                tint = scheme.primary
            )
        )
    }

    DashboardSection(
        title = "Family Overview",
        subtitle = "Monitor learning, discipline, and school communication in one place",
        stats = stats,
        insights = insights,
        modifier = modifier
    )
}

@Composable
fun TeacherOverviewSection(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val stats = remember {
        listOf(
            DashboardStat("Classes Today", "6", "First lesson starts at 08:00", Icons.Default.Schedule, scheme.primary),
            DashboardStat("Ungraded Work", "28", "Across 3 assessments", Icons.Default.PendingActions, Color(0xFFC77700)),
            DashboardStat("Attendance Tasks", "2", "Need submission before noon", Icons.Default.CheckCircle, Color(0xFF1E8E5A)),
            DashboardStat("Parent Messages", "7", "Conversation follow-ups pending", Icons.AutoMirrored.Filled.Chat, scheme.secondary)
        )
    }
    val insights = remember {
        listOf(
            DashboardInsight(
                title = "Teaching Focus",
                body = "Your workload is heaviest around grading and parent communication, not classroom schedule.",
                meta = "Work queue summary",
                icon = Icons.Default.LocalLibrary,
                tint = scheme.primary
            ),
            DashboardInsight(
                title = "Best Use Of Time",
                body = "Finish the two overdue grade batches first, then clear unread parent conversations before the last period.",
                meta = "Recommended order",
                icon = Icons.Default.AssignmentTurnedIn,
                tint = scheme.secondary
            )
        )
    }

    DashboardSection(
        title = "Teaching Overview",
        subtitle = "A quick read on classroom load, grading pressure, and communication",
        stats = stats,
        insights = insights,
        modifier = modifier
    )
}

@Composable
fun AdminOverviewSection(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val stats = remember {
        listOf(
            DashboardStat("Pending Approvals", "9", "Role, leave, and event requests", Icons.Default.PendingActions, scheme.primary),
            DashboardStat("Grade Backlog", "31", "Across 4 active classes", Icons.Default.TrendingUp, Color(0xFFC77700)),
            DashboardStat("Discipline Alerts", "4", "Cards below warning threshold", Icons.Default.Rule, scheme.error),
            DashboardStat("Payment Reviews", "6", "Bank transfers awaiting verification", Icons.Default.Payments, Color(0xFF1E8E5A))
        )
    }
    val insights = remember {
        listOf(
            DashboardInsight(
                title = "Operations Pulse",
                body = "The school is stable overall, but grading backlog and discipline follow-up need action before they spread.",
                meta = "Admin summary",
                icon = Icons.Default.Badge,
                tint = scheme.primary
            ),
            DashboardInsight(
                title = "Best Admin Moves",
                body = "Resolve approvals, review fee verifications, and escalate the four discipline cases below policy threshold.",
                meta = "Recommended now",
                icon = Icons.Default.School,
                tint = scheme.secondary
            )
        )
    }

    DashboardSection(
        title = "Admin Overview",
        subtitle = "Critical school operations, approvals, discipline, and finance at a glance",
        stats = stats,
        insights = insights,
        modifier = modifier
    )
}

@Composable
fun AdminOperationsBoard(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val items = remember {
        listOf(
            DashboardInsight(
                title = "Grade Review Queue",
                body = "S4 Science Physics, S2 English, and S3 Maths have pending mark publication.",
                meta = "31 items pending",
                icon = Icons.Default.TrendingUp,
                tint = Color(0xFFC77700)
            ),
            DashboardInsight(
                title = "Discipline Follow-up",
                body = "4 students are nearing the warning threshold and need summary review with class staff.",
                meta = "2 updated today",
                icon = Icons.Default.Rule,
                tint = scheme.error
            ),
            DashboardInsight(
                title = "Approval Desk",
                body = "Leave request, debate club event, and two role requests are still waiting for action.",
                meta = "9 active approvals",
                icon = Icons.Default.PendingActions,
                tint = scheme.primary
            ),
            DashboardInsight(
                title = "Finance Verification",
                body = "6 bank payments were posted and need confirmation against school accounts.",
                meta = "Last sync 10 min ago",
                icon = Icons.Default.Payments,
                tint = Color(0xFF1E8E5A)
            )
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("Operations Board")
        Spacer(modifier = Modifier.height(10.dp))
        items.forEachIndexed { index, item ->
            InsightCard(item)
            if (index != items.lastIndex) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun AdminPendingRequestsSection(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val inboxItems = remember {
        listOf(
            AdminRequestInboxItem(
                id = "req_teacher_role",
                title = "Teacher role request",
                requesterLabel = "Niyonzima Claude",
                conversationHint = "Teacher access verification conversation",
                statusLabel = "Need review",
                icon = Icons.Default.School,
                tint = scheme.primary,
                note = "Documents, clarifications, and the verification call should continue in the same conversation."
            ),
            AdminRequestInboxItem(
                id = "req_parent_link",
                title = "Trusted adult child link",
                requesterLabel = "Mukamana Alice",
                conversationHint = "Guardian consent thread",
                statusLabel = "Need documents",
                icon = Icons.Default.Groups,
                tint = scheme.secondary,
                note = "Request guardian consent and supporting evidence inside the request conversation."
            ),
            AdminRequestInboxItem(
                id = "req_school_admin",
                title = "School admin access",
                requesterLabel = "Habimana Eric",
                conversationHint = "Campus authorization thread",
                statusLabel = "Waiting reply",
                icon = Icons.Default.Badge,
                tint = Color(0xFFC77700),
                note = "Use the conversation to request the signed authorization and track uploads."
            )
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("Requests Inbox")
        Spacer(modifier = Modifier.height(10.dp))
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = scheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "Keep approvals inside request conversations",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Home only shows what needs attention. The real approve, reject, ask-for-documents, and verification flow should live in the conversation history.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(scheme.primary.copy(alpha = 0.10f), RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = scheme.primary
                        )
                    }
                }

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InboxStatPill(label = "Pending", value = "3", tint = scheme.primary)
                    InboxStatPill(label = "Need docs", value = "2", tint = scheme.tertiary)
                    InboxStatPill(label = "Call today", value = "1", tint = scheme.secondary)
                }

                if (inboxItems.isEmpty()) {
                    EmptyRequestInboxState()
                } else {
                    inboxItems.forEachIndexed { index, item ->
                        RequestConversationPreview(item = item)
                        if (index != inboxItems.lastIndex) {
                            HorizontalDivider(color = scheme.outlineVariant.copy(alpha = 0.5f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InboxStatPill(label: String, value: String, tint: Color) {
    Row(
        modifier = Modifier
            .background(tint.copy(alpha = 0.10f), RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = tint
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = tint
        )
    }
}

@Composable
private fun RequestConversationPreview(item: AdminRequestInboxItem) {
    val scheme = MaterialTheme.colorScheme

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(item.tint.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.tint
            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.statusLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = item.tint
                )
            }
            Text(
                text = item.requesterLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant
            )
            Text(
                text = item.conversationHint,
                style = MaterialTheme.typography.labelLarge,
                color = scheme.primary
            )
            Text(
                text = item.note,
                style = MaterialTheme.typography.bodySmall,
                color = scheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyRequestInboxState() {
    val scheme = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(Icons.Default.HourglassEmpty, null, tint = scheme.outline.copy(alpha = 0.24f), modifier = Modifier.size(26.dp))
                Icon(Icons.AutoMirrored.Filled.Chat, null, tint = scheme.outline.copy(alpha = 0.24f), modifier = Modifier.size(26.dp))
                Icon(Icons.Default.Verified, null, tint = scheme.outline.copy(alpha = 0.24f), modifier = Modifier.size(26.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "No request conversations need attention",
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.outline
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "When a role request, document follow-up, or verification call is pending, it should appear here as a conversation to open.",
                style = MaterialTheme.typography.bodySmall,
                color = scheme.outline,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
    }
}

@Composable
private fun DashboardSection(
    title: String,
    subtitle: String,
    stats: List<DashboardStat>,
    insights: List<DashboardInsight>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppSubHeader(title)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            stats.forEach { stat ->
                StatCard(stat = stat, modifier = Modifier.fillMaxWidth(0.48f))
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        insights.forEachIndexed { index, insight ->
            InsightCard(insight)
            if (index != insights.lastIndex) {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
private fun StatCard(
    stat: DashboardStat,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(stat.tint.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(stat.icon, contentDescription = null, tint = stat.tint)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = stat.value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stat.label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stat.note,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InsightCard(insight: DashboardInsight) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        listOf(
                            insight.tint.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.surfaceContainerLow
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(insight.tint.copy(alpha = 0.14f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(insight.icon, contentDescription = null, tint = insight.tint)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = insight.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = insight.meta,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = insight.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
