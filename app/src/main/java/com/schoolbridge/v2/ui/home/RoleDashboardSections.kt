package com.schoolbridge.v2.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalLibrary
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Rule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
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

private enum class ApprovalState(val label: String) {
    Pending("Pending"),
    NeedsInfo("Need Info"),
    Approved("Approved"),
    Rejected("Rejected")
}

private data class AdminApprovalRequest(
    val id: String,
    val title: String,
    val requester: String,
    val detail: String,
    val evidence: String,
    val icon: ImageVector,
    val tint: Color,
    val state: ApprovalState = ApprovalState.Pending,
    val nextStep: String = "Review the evidence and decide the next action."
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
    val requests = remember {
        mutableStateListOf(
            AdminApprovalRequest(
                id = "req_teacher_role",
                title = "Teacher role request",
                requester = "Niyonzima Claude",
                detail = "Requested teacher access for Senior 3 Maths and Physics at Kigali High School.",
                evidence = "Submitted school email, timetable excerpt, and staff ID snapshot.",
                icon = Icons.Default.School,
                tint = scheme.primary,
                nextStep = "Approve if the staff ID and subject load match the school records."
            ),
            AdminApprovalRequest(
                id = "req_parent_link",
                title = "Trusted adult child link",
                requester = "Mukamana Alice",
                detail = "Asked to link Uwase Clarisse as an additional trusted adult for student Sandrine Uwase.",
                evidence = "Relationship note says aunt and emergency pickup helper; school card copy still missing.",
                icon = Icons.Default.PersonAdd,
                tint = scheme.secondary,
                nextStep = "Request guardian consent and at least one supporting document before approval."
            ),
            AdminApprovalRequest(
                id = "req_school_admin",
                title = "School admin access",
                requester = "Habimana Eric",
                detail = "Requested operations access for Nyagatare campus finance and approvals desk.",
                evidence = "Uploaded appointment letter, but no signed authorization from current head teacher yet.",
                icon = Icons.Default.Badge,
                tint = Color(0xFFC77700),
                nextStep = "Ask for the signed authorization letter and route a chat thread for document upload."
            )
        )
    }

    Column(modifier = modifier.fillMaxWidth()) {
        AppSubHeader("Approval Desk")
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "School admins should be able to approve, reject, or request more info without leaving the dashboard.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(14.dp))
        requests.forEachIndexed { index, request ->
            AdminApprovalCard(
                request = request,
                onApprove = {
                    val i = requests.indexOfFirst { item -> item.id == request.id }
                    if (i >= 0) {
                        requests[i] = requests[i].copy(
                            state = ApprovalState.Approved,
                            nextStep = "Access can now be granted and the requester should receive a confirmation notice."
                        )
                    }
                },
                onReject = {
                    val i = requests.indexOfFirst { item -> item.id == request.id }
                    if (i >= 0) {
                        requests[i] = requests[i].copy(
                            state = ApprovalState.Rejected,
                            nextStep = "A rejection note should explain what failed and whether the requester may reapply."
                        )
                    }
                },
                onRequestInfo = {
                    val i = requests.indexOfFirst { item -> item.id == request.id }
                    if (i >= 0) {
                        requests[i] = requests[i].copy(
                            state = ApprovalState.NeedsInfo,
                            nextStep = "Open a chat thread with upload actions so the requester can send the missing documents."
                        )
                    }
                }
            )
            if (index != requests.lastIndex) {
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun AdminApprovalCard(
    request: AdminApprovalRequest,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onRequestInfo: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val stateTint = when (request.state) {
        ApprovalState.Pending -> request.tint
        ApprovalState.NeedsInfo -> scheme.tertiary
        ApprovalState.Approved -> Color(0xFF1E8E5A)
        ApprovalState.Rejected -> scheme.error
    }

    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = scheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(request.tint.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = request.icon,
                            contentDescription = null,
                            tint = request.tint
                        )
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = request.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = request.requester,
                            style = MaterialTheme.typography.bodyMedium,
                            color = scheme.onSurfaceVariant
                        )
                    }
                }
                AssistChip(
                    onClick = {},
                    label = { Text(request.state.label) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = stateTint.copy(alpha = 0.12f),
                        labelColor = stateTint
                    )
                )
            }

            Text(
                text = request.detail,
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.onSurfaceVariant
            )

            MetaCallout(
                icon = Icons.Default.Description,
                title = "Evidence",
                body = request.evidence,
                tint = scheme.secondary
            )

            MetaCallout(
                icon = Icons.Default.Info,
                title = "Next step",
                body = request.nextStep,
                tint = stateTint
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onApprove,
                    enabled = request.state != ApprovalState.Approved,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E8E5A)),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Approve")
                }
                OutlinedButton(
                    onClick = onRequestInfo,
                    enabled = request.state != ApprovalState.NeedsInfo,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Ask Info")
                }
                OutlinedButton(
                    onClick = onReject,
                    enabled = request.state != ApprovalState.Rejected,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = scheme.error)
                ) {
                    Text("Reject")
                }
            }
        }
    }
}

@Composable
private fun MetaCallout(
    icon: ImageVector,
    title: String,
    body: String,
    tint: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, tint.copy(alpha = 0.18f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp)
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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
