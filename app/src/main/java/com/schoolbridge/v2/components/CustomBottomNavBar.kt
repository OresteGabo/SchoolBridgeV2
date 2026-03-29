package com.schoolbridge.v2.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.schoolbridge.v2.domain.user.CurrentUser
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.common.tutorial.CoachMarkTargetRegistry
import com.schoolbridge.v2.ui.common.tutorial.coachMarkTarget
import com.schoolbridge.v2.ui.navigation.MainAppScreen

@Composable
fun CustomBottomNavBar(
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    currentUser: CurrentUser? = null,
    tutorialRegistry: CoachMarkTargetRegistry? = null
) {
    val items = rememberBottomNavItems(currentUser)
    NavigationContainer(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items.forEach { screen ->
                BottomNavItem(
                    screen = screen,
                    selected = currentScreen.route == screen.route,
                    onClick = { onTabSelected(screen) },
                    modifier = Modifier
                        .weight(1f)
                        .coachMarkTarget("bottom_nav_${screen.route}", tutorialRegistry)
                )
            }
        }
    }
}

@Composable
fun CustomSideNavBar(
    currentScreen: MainAppScreen,
    onTabSelected: (MainAppScreen) -> Unit,
    currentUser: CurrentUser? = null,
    tutorialRegistry: CoachMarkTargetRegistry? = null,
    modifier: Modifier = Modifier,
    width: Dp = 124.dp
) {
    val items = rememberBottomNavItems(currentUser)
    NavigationContainer(
        modifier = modifier
            .fillMaxHeight()
            .width(width)
            .padding(start = 12.dp, top = 18.dp, bottom = 18.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Spacer(modifier = Modifier.size(2.dp))
            items.forEach { screen ->
                SideNavItem(
                    screen = screen,
                    selected = currentScreen.route == screen.route,
                    onClick = { onTabSelected(screen) },
                    modifier = Modifier.coachMarkTarget("bottom_nav_${screen.route}", tutorialRegistry)
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun NavigationContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Surface(
        color = Color.Transparent,
        shadowElevation = 0.dp,
        modifier = modifier
    ) {
        Box(
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
        ) {
            content()
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
    NavigationItemFrame(
        selected = selected,
        onClick = onClick,
        modifier = modifier
    ) { contentColor ->
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
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
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
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
private fun SideNavItem(
    screen: MainAppScreen,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationItemFrame(
        selected = selected,
        onClick = onClick,
        modifier = modifier
    ) { contentColor ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val icon = if (selected) selectedNavIcon(screen) else unselectedNavIcon(screen)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else Color.Transparent
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
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp)
            )
            if (selected) {
                Box(
                    modifier = Modifier
                        .padding(top = 6.dp)
                        .size(width = 18.dp, height = 3.dp)
                        .clip(RoundedCornerShape(999.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
private fun NavigationItemFrame(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (contentColor: Color) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val containerColor by animateColorAsState(
        targetValue = if (selected) colors.primaryContainer else Color.Transparent,
        label = "navItemContainer"
    )
    val contentColor by animateColorAsState(
        targetValue = if (selected) colors.onPrimaryContainer else colors.onSurfaceVariant,
        label = "navItemContent"
    )

    Surface(
        onClick = onClick,
        color = containerColor,
        shape = RoundedCornerShape(20.dp),
        tonalElevation = if (selected) 3.dp else 0.dp,
        modifier = modifier
    ) {
        content(contentColor)
    }
}

fun rememberBottomNavItems(currentUser: CurrentUser?): List<MainAppScreen> {
    val left = buildList {
        add(MainAppScreen.Message)
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
private fun navLabel(screen: MainAppScreen): String =
    screen.titleRes?.let { t(it) } ?: screen.route

private fun selectedNavIcon(screen: MainAppScreen): ImageVector =
    screen.selectedIcon ?: Icons.Filled.Home

private fun unselectedNavIcon(screen: MainAppScreen): ImageVector =
    screen.unselectedIcon ?: screen.selectedIcon ?: Icons.Outlined.Home
