package com.schoolbridge.v2.ui.navigation

import android.net.Uri
import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CurrencyFranc
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.CurrencyFranc
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.ui.graphics.vector.ImageVector
import com.schoolbridge.v2.R

sealed class MainAppScreen(
    val route: String,
    @StringRes val titleRes: Int? = null,
    val selectedIcon: ImageVector? = null,
    val unselectedIcon: ImageVector? = null,
    val hasNews: Boolean = false
)
{
    data object Home : MainAppScreen(
        route = "home_screen",
        titleRes = R.string.home_label,
        selectedIcon = Icons.Filled.Home,
        unselectedIcon = Icons.Outlined.Home
    )

    data object Message : MainAppScreen(
        route = "message_screen",
        titleRes = R.string.message_label,
        selectedIcon = Icons.AutoMirrored.Filled.Chat,
        unselectedIcon = Icons.AutoMirrored.Outlined.Chat
    )

    data object Finance : MainAppScreen(
        route = "finance_screen",
        titleRes = R.string.finance_label,
        selectedIcon = Icons.Filled.CurrencyFranc,
        unselectedIcon = Icons.Outlined.CurrencyFranc
    )

    data object Profile : MainAppScreen("profile_screen")
    data object Notifications : MainAppScreen("notifications_screen")
    data object Settings : MainAppScreen("settings_screen")
    data object Language : MainAppScreen("language_setting_screen")
    data object Events : MainAppScreen("events_screen")
    data object Theme : MainAppScreen("theme_setting_screen")
    data object HelpFAQ : MainAppScreen("help_faq_screen")
    data object About : MainAppScreen("about_screen")
    data object DataPrivacy : MainAppScreen("data_privacy_screen")

    data object WeeklySchedule : MainAppScreen(
        route = "weekly_schedule_screen",
        titleRes = R.string.schedule_label,
        selectedIcon = Icons.Filled.CalendarMonth,
        unselectedIcon = Icons.Outlined.CalendarMonth
    )

    data object Alerts : MainAppScreen(
        route = "alerts_screen",
        titleRes = R.string.alerts_label,
        selectedIcon = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )

    data object Terms : MainAppScreen("terms_screen")
    data object PrivacyPolicy : MainAppScreen("privacy_policy_screen")

    data object VerificationScreen : MainAppScreen("verification_screen")
    data object CoursesList : MainAppScreen("courses_list_screen")

    data object CourseDetail : MainAppScreen("course_detail_screen/{courseId}") {
        const val COURSE_ID_ARG = "courseId"
        const val ROUTE_PATTERN = "course_detail_screen/{courseId}"

        fun createRoute(courseId: String) = "course_detail_screen/$courseId"
    }

    data object GradesList : MainAppScreen("grades_list_screen")

    data object EvaluationDetail : MainAppScreen("evaluation_detail_screen/{evaluationId}") {
        const val EVALUATION_ID_ARG = "evaluationId"
        const val ROUTE_PATTERN = "evaluation_detail_screen/{evaluationId}"

        fun createRoute(evaluationId: String) = "evaluation_detail_screen/$evaluationId"
    }


    data object EventDetails : MainAppScreen("event_details_screen/{eventId}") {
        const val ROUTE_PATTERN = "event_details_screen/{eventId}"
        const val EVENT_ID_ARG = "eventId"

        fun createRoute(eventId: String) = "event_details_screen/$eventId"
    }

    data object MessageConversationDetails : MainAppScreen("message_conversation_screen/{messageConversationId}?callMessageId={callMessageId}") {
        const val ROUTE_PATTERN = "message_conversation_screen/{messageConversationId}?callMessageId={callMessageId}"
        const val MESSAGE_CONVERSATION_ID_ARG = "messageConversationId"
        const val CALL_MESSAGE_ID_ARG = "callMessageId"

        fun createRoute(messageConversationId: String, callMessageId: String? = null): String {
            val encodedConversationId = Uri.encode(messageConversationId)
            val encodedCallMessageId = callMessageId?.let(Uri::encode)
            return if (encodedCallMessageId == null) {
                "message_conversation_screen/$encodedConversationId"
            } else {
                "message_conversation_screen/$encodedConversationId?callMessageId=$encodedCallMessageId"
            }
        }
    }

    data object MessageDetails : MainAppScreen("message_details_screen/{messageId}") {
        const val ROUTE_PATTERN = "message_details_screen/{messageId}"
        const val MESSAGE_ID_ARG = "messageId"

        fun createRoute(messageId: String) = "message_details_screen/$messageId"
    }

    data object StudentDashboard : MainAppScreen("student_dashboard_screen")
    data object ParentChildrenList : MainAppScreen("parent_children_list_screen")
    data object TeacherAssignedCourses : MainAppScreen("teacher_assigned_courses_screen")

    data object RequestRole : MainAppScreen("request_role_screen")
    data object RequestStudentRole: MainAppScreen("request_student_role")
    data object RequestParentRole: MainAppScreen("request_parent_role")
    data object RequestTeacherRole: MainAppScreen("request_teachert_role")
    data object RequestSchoolAdminRole: MainAppScreen("request_school_admin_role")
}
