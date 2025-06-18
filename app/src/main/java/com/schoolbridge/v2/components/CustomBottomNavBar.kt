package com.schoolbridge.v2.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.schoolbridge.v2.localization.t
import com.schoolbridge.v2.ui.navigation.MainAppScreen
import com.schoolbridge.v2.ui.onboarding.shared.MainNavScreen

@Composable
fun CustomBottomNavBar(
    currentScreen: MainAppScreen,                 // <- same sealed class you show below
    onTabSelected: (MainAppScreen) -> Unit
) {
    // List the tabs you want to appear, in order
    val items = listOf(
        MainAppScreen.Message,
        MainAppScreen.Home,
        MainAppScreen.Finance,
    )

    NavigationBar {
        items.forEach { screen ->

            // Is this the currently‑selected tab?
            val selected = currentScreen.route == screen.route

            NavigationBarItem(
                selected = selected,                               // ░░ selected ░░
                onClick  = { onTabSelected(screen) },              // ░░ onClick  ░░

                icon = {                                           // ░░ icon ░░
                    Icon(
                        imageVector =
                            if (selected) screen.selectedIcon!!    // pick filled / outlined icon
                            else            screen.unselectedIcon!!,
                        contentDescription = screen.title?.let {  // null‑safe C.D.
                            t(it)
                        }
                    )
                },

                modifier          = Modifier,                      // leave default
                enabled           = true,                          // always tappable
                label = {
                    if(screen.title != null)// ░░ label ░░
                        Text(text = t(screen.title))
                    else Text("")
                },
                alwaysShowLabel   = true,                         // show label only when selected
                colors            = NavigationBarItemDefaults
                    .colors(),                 // material‑3 defaults
                interactionSource = remember {                     // smooth ripple / press state
                    MutableInteractionSource()
                }
            )
        }
    }
}
