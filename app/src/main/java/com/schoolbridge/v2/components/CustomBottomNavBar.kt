package com.schoolbridge.v2.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyFranc
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CurrencyFranc
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.R
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.navigation.MainAppScreen

@Composable
fun CustomBottomNavBar(
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    currentUser: CurrentUser? = null
) {
    val items = rememberBottomNavItems(currentUser)
    val colors = MaterialTheme.colorScheme

    Surface(
        color = Color.Transparent,
        shadowElevation = 0.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(26.dp))
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            colors.surface.copy(alpha = 0.98f),
                            colors.surfaceContainer.copy(alpha = 0.94f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = colors.outlineVariant.copy(alpha = 0.45f),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { screen ->
                val selected = currentScreen.route == screen.route
                BottomNavItem(
                    screen = screen,
                    selected = selected,
                    onClick = { onTabSelected(screen) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    screen: MainAppScreen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val containerColor by animateColorAsState(
        targetValue = if (selected) colors.primaryContainer else Color.Transparent,
        label = "bottomNavContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
        label = "bottomNavContent"
    )

    Surface(
        onClick = onClick,
        color = containerColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = if (selected) 3.dp else 0.dp,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val icon = if (selected) selectedNavIcon(screen) else unselectedNavIcon(screen)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) colors.primary.copy(alpha = 0.12f) else Color.Transparent
                    )
                    .padding(8.dp)
            ) {
            Icon(
                imageVector = icon,
                contentDescription = navLabel(screen),
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            }
            Text(
                text = navLabel(screen),
                color = contentColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .padding(top = 5.dp)
                        .size(width = 16.dp, height = 3.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(colors.primary)
                )
            }
        }
    }
}

private fun rememberBottomNavItems(currentUser: CurrentUser?): List<MainAppScreen> {
    val left = buildList {
        add(MainAppScreen.Message)
        if (currentUser?.isTeacher() == true || currentUser?.isAdmin() == true) {
            add(MainAppScreen.Alerts)
        }
    }

    val right = buildList {
        if (currentUser == null || currentUser.isTeacher() || currentUser.isStudent() || currentUser.isParent() || currentUser.isAdmin()) {
            add(MainAppScreen.WeeklySchedule)
        }
        if (currentUser == null || currentUser.isParent() || currentUser.isStudent() || currentUser.isAdmin()) {
            add(MainAppScreen.Finance)
        }
    }

    return (left + MainAppScreen.Home + right).distinctBy { it.route }.take(5)
}

@Composable
private fun navLabel(screen: MainAppScreen): String = when (screen) {
    MainAppScreen.Message -> t(screen.title ?: error("Missing title"))
    MainAppScreen.Home -> t(screen.title ?: error("Missing title"))
    MainAppScreen.Finance -> t(screen.title ?: error("Missing title"))
    MainAppScreen.WeeklySchedule -> t(R.string.schedule_label)
    MainAppScreen.Alerts -> t(R.string.alerts_label)
    else -> screen.route
}

private fun selectedNavIcon(screen: MainAppScreen): ImageVector = when (screen) {
    MainAppScreen.WeeklySchedule -> Icons.Filled.CalendarMonth
    MainAppScreen.Alerts -> Icons.Filled.Notifications
    MainAppScreen.Home -> Icons.Filled.Home
    MainAppScreen.Finance -> Icons.Filled.CurrencyFranc
    else -> screen.selectedIcon ?: Icons.Filled.Home
}

private fun unselectedNavIcon(screen: MainAppScreen): ImageVector = when (screen) {
    MainAppScreen.WeeklySchedule -> Icons.Outlined.CalendarMonth
    MainAppScreen.Alerts -> Icons.Outlined.Notifications
    MainAppScreen.Home -> Icons.Outlined.Home
    MainAppScreen.Finance -> Icons.Outlined.CurrencyFranc
    else -> screen.unselectedIcon ?: screen.selectedIcon ?: Icons.Outlined.Home
}
