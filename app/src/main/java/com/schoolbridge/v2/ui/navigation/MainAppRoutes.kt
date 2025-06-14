package com.schoolbridge.v2.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.CurrencyFranc
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CurrencyFranc
import androidx.compose.material.icons.outlined.Home
import androidx.compose.ui.graphics.vector.ImageVector
import com.schoolbridge.v2.R

/**
 * Defines the sealed class for all main application routes.
 * These screens are accessed after a user is authenticated and are often part of a persistent
 * navigation structure (e.g., bottom navigation bar, navigation drawer, or deep links).
 *
 * @param route The unique string identifier for the navigation destination. This is what you use
 * in `navController.navigate(route)`.
 * @param title A string resource ID for the screen's title, often used for bottom navigation labels
 * or app bar titles. Nullable if the screen doesn't require a direct display title.
 * @param selectedIcon The [ImageVector] icon to display when this navigation item is selected
 * (e.g., in a Bottom Navigation Bar). Nullable if not a direct nav item.
 * @param unselectedIcon The [ImageVector] icon to display when this navigation item is not selected.
 * Nullable if not a direct nav item.
 * @param hasNews A boolean flag indicating if there's new content or notifications for this section.
 * Can be used to display a badge on navigation items.
 */
sealed class MainAppScreen(
    val route: String, // This 'route' refers to the concrete instance's route
    val title: Int? = null,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    val hasNews: Boolean = false,
) {
    // --- Main Bottom Navigation / Top-Level Items ---
    // These typically have associated icons and titles for primary navigation elements.
    data object Home : MainAppScreen(
        route = "home_screen",
        title = R.string.home_label,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home,
    )

    data object Message : MainAppScreen(
        route = "message_screen",
        title = R.string.message_label,
        selectedIcon = Icons.AutoMirrored.Filled.Chat,
        unselectedIcon = Icons.AutoMirrored.Outlined.Chat,
        // hasNews could be updated dynamically via a ViewModel reacting to new messages
    )

    data object Finance : MainAppScreen(
        route = "finance_screen",
        title = R.string.finance_label,
        selectedIcon = Icons.Filled.CurrencyFranc, // Ensure you have this icon or a similar one
        unselectedIcon = Icons.Outlined.CurrencyFranc,
    )

    // --- Other Main App Screens (not necessarily on a bottom navigation bar) ---
    // These might be accessible via settings, deep links, navigation drawer, or specific feature flows.
    data object Profile : MainAppScreen("profile_screen") // Changed to data object
    data object Notifications : MainAppScreen("notifications_screen") // Changed to data object

    // **Updated Settings-related screens to use data object for consistency**
    data object Settings : MainAppScreen("settings_screen") // The main settings list screen
    data object Language : MainAppScreen("language_setting_screen")
    data object Theme : MainAppScreen("theme_setting_screen")
    data object HelpFAQ : MainAppScreen("help_faq_screen")
    data object About : MainAppScreen("about_screen")
    data object DataPrivacy : MainAppScreen("data_privacy_screen") // Distinct from onboarding's PrivacyPolicy

    data object Terms : MainAppScreen("terms_screen")
    data object PrivacyPolicy : MainAppScreen("privacy_policy_screen")

    // --- Academic Sections (examples of nested routes or detail screens) ---
    data object CoursesList : MainAppScreen("courses_list_screen") // Changed to data object

    /**
     * Route for displaying details of a specific course, identified by its ID.
     * Example usage for navigation: `navController.navigate(MainAppScreen.CourseDetail.createRoute("course_123"))`
     */
    data class CourseDetail(val courseId: String) : MainAppScreen(CourseDetail.ROUTE_PATTERN) {
        companion object {
            // Define the route pattern as a constant in a companion object
            const val ROUTE_PATTERN = "course_detail_screen/{courseId}"
        }
        fun createRoute(courseId: String) = "course_detail_screen/$courseId"
    }

    data object GradesList : MainAppScreen("grades_list_screen") // Changed to data object

    /**
     * Route for displaying details of a specific evaluation, identified by its ID.
     * Example usage: `navController.navigate(MainAppScreen.EvaluationDetail.createRoute("eval_abc"))`
     */
    data class EvaluationDetail(val evaluationId: String) : MainAppScreen(EvaluationDetail.ROUTE_PATTERN) {
        companion object {
            // Define the route pattern as a constant in a companion object
            const val ROUTE_PATTERN = "evaluation_detail_screen/{evaluationId}"
        }
        fun createRoute(evaluationId: String) = "evaluation_detail_screen/$evaluationId"
    }


    /**
     * Route for displaying details of a specific event, identified by its ID.
     * Example usage: `navController.navigate(MainAppScreen.EventDetails.createRoute("event_xyz"))`
     */
    data class EventDetails(val eventId: String) : MainAppScreen(EventDetails.ROUTE_PATTERN) {
        companion object {
            const val ROUTE_PATTERN = "event_details_screen/{eventId}"
            const val EVENT_ID_ARG = "eventId" // Define argument key for clarity
            fun createRoute(eventId: String) = "event_details_screen/$eventId"
        }
    }


    // --- User-specific dashboards/lists (for different roles) ---
    data object StudentDashboard : MainAppScreen("student_dashboard_screen") // Changed to data object
    data object ParentChildrenList : MainAppScreen("parent_children_list_screen") // Changed to data object
    data object TeacherAssignedCourses : MainAppScreen("teacher_assigned_courses_screen") // Changed to data object

    // TODO: Add more specific routes as your application's features are implemented.
}