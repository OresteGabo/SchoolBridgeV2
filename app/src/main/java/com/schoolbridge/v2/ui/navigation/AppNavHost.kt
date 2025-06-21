// src/main/java/com/schoolbridge/v2/ui/navigation/AppNavHost.kt
package com.schoolbridge.v2.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.schoolbridge.v2.data.remote.AuthApiService
import com.schoolbridge.v2.data.session.UserSessionManager

import com.schoolbridge.v2.ui.finance.FinanceScreen
import com.schoolbridge.v2.ui.home.HomeRoute
import com.schoolbridge.v2.ui.onboarding.auth.LoginScreen
import com.schoolbridge.v2.ui.onboarding.auth.LoginViewModelFactory
import com.schoolbridge.v2.ui.settings.SettingsScreen
import com.schoolbridge.v2.ui.settings.profile.ProfileScreen

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.navigation.NavGraph.Companion.findStartDestination // Needed for popUpTo graph ID
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.schoolbridge.v2.data.preferences.ThemePreferenceManager
import com.schoolbridge.v2.ui.event.EventDetailsRoute
import com.schoolbridge.v2.ui.event.EventRepository
import com.schoolbridge.v2.ui.onboarding.auth.CredentialsSetupScreen
import com.schoolbridge.v2.ui.onboarding.auth.SignUpScreen
import com.schoolbridge.v2.ui.onboarding.legal.TermsOfServiceScreen
import com.schoolbridge.v2.ui.settings.about.AboutScreen
import com.schoolbridge.v2.ui.settings.dataprivacy.DataPrivacySettingsScreen
import com.schoolbridge.v2.ui.settings.help.HelpFAQScreen
import com.schoolbridge.v2.ui.settings.notifications.NotificationSettingsScreen
import com.schoolbridge.v2.ui.theme.ThemeViewModel
import androidx.compose.runtime.getValue
import com.schoolbridge.v2.domain.messaging.MessageThreadRepository
import com.schoolbridge.v2.ui.home.alert.AlertsScreen
import com.schoolbridge.v2.ui.home.alert.EventsScreen
import com.schoolbridge.v2.ui.home.timetable.TimetableTabsScreen
import com.schoolbridge.v2.ui.message.MessageScreen
import com.schoolbridge.v2.ui.message.MessageThreadScreen
import com.schoolbridge.v2.ui.onboarding.shared.MainNavScreen

/**
 * The main navigation host for the SchoolBridge V2 application.
 * This composable defines all the navigation routes and their associated UI screens.
 *
 * @param navController The [NavHostController] instance that manages navigation within the host.
 * If null, a default one is remembered.
 * @param startDestination The route to the initial screen when the app starts.
 * @param authApiService The API service for authentication.
 * @param userSessionManager Manages the user's session data.
 * @param modifier The modifier to be applied to the layout.
 */
@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
    authApiService: AuthApiService,
    userSessionManager: UserSessionManager,
    themeViewModel: ThemeViewModel,               // Added
    themePreferenceManager: ThemePreferenceManager, // Added
    modifier: Modifier = Modifier
) {
    val isDarkTheme by themeViewModel.isDarkTheme.collectAsState()
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Authentication/Onboarding Flow ---
        composable(AuthScreen.Onboarding.route) {
            // TODO: Implement your OnboardingScreen composable
            // OnboardingScreen(onFinishOnboarding = { navController.navigate(AuthScreen.Login.route) })
            println("Navigated to Onboarding Screen") // Placeholder for OnboardingScreen
        }

        composable(AuthScreen.Login.route) {
            LoginScreen(
                navigateToHome = {
                    navController.navigate(MainAppScreen.Home.route) {
                        // Clear login from back stack to prevent going back to it
                        popUpTo(AuthScreen.Login.route) { inclusive = true }
                    }
                },
                viewModel = viewModel(
                    factory = LoginViewModelFactory(
                        authApiService,
                        userSessionManager
                    )
                ),
                authApiService = authApiService,
                userSessionManager = userSessionManager,
                onForgotPassword = {navController.navigate(AuthScreen.ForgotPassword.route)},
                onCreateAccount = { navController.navigate(AuthScreen.SignUp.route) }
            )
        }

        composable(AuthScreen.SignUp.route) {
            // TODO: Implement your SignUpScreen composable
             SignUpScreen(
                 navController = navController,
                 onContinueAsGuest = {},
                 onTermsClick = {},
                 onPrivacyClick = {},
                 onNext = { firstName, lastName, phone, gender ->
                     navController.navigate(AuthScreen.CredentialsSetup.route)
                 },
                 onPrivacyPolicyClick = {}
             )
            println("Navigated to Sign Up Screen") // Placeholder for SignUpScreen
        }

        composable(AuthScreen.CredentialsSetup.route) {
            CredentialsSetupScreen(
                onContinue = {},
                modifier = Modifier
            )
        }

        composable(AuthScreen.ForgotPassword.route) {
            // TODO: Implement your ForgotPasswordScreen composable
            // ForgotPasswordScreen(onResetSuccess = { navController.navigate(AuthScreen.Login.route) })
            println("Navigated to Forgot Password Screen") // Placeholder for ForgotPasswordScreen
        }


        // --- Main Application Flow (Post-Login) ---
        // These use the routes defined in MainAppScreen
        composable(MainAppScreen.Home.route) {
            HomeRoute(
                userSessionManager = userSessionManager,
                onSettingsClick = {
                    navController.navigate(MainAppScreen.Settings.route)
                },
                onEventClick = { eventId ->
                    // Use the createRoute function for type-safe navigation
                    navController.navigate(MainAppScreen.EventDetails.createRoute(eventId))
                },
                modifier = modifier,
                onViewAllAlertsClick = {
                    navController.navigate(MainAppScreen.Alerts.route)
                },
                onViewAllEventsClick = {
                    navController.navigate(MainAppScreen.Events.route)
                },
                currentScreen = MainAppScreen.Home, // ✅ This is the current tab
                onTabSelected = { screen ->         // ✅ Handle bottom tab navigation
                    navController.navigate(screen.route) {
                        launchSingleTop = true      // Prevent multiple copies
                        restoreState = true         // Restore scroll position, etc.
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true       // Keep previous screen state
                        }
                    }
                },
                onWeeklyViewClick = {
                    navController.navigate(MainAppScreen.WeeklySchedule.route)
                }
            )
        }

        composable(MainAppScreen.WeeklySchedule.route){
            TimetableTabsScreen(
                onBack = { navController.navigateUp() },
            )
        }

        composable(MainAppScreen.Alerts.route){
            AlertsScreen(onBack = { navController.navigateUp() })
        }
        composable(MainAppScreen.Events.route){
            EventsScreen(
                onBack = { navController.navigateUp() },
                onEventClick = { eventId ->
                    // Use the createRoute function for type-safe navigation
                    navController.navigate(MainAppScreen.EventDetails.createRoute(eventId))
                },

            )
        }


        // --- Event Details Route (now using MainAppScreen.EventDetails) ---
        composable(
            route = MainAppScreen.EventDetails.ROUTE_PATTERN, // Use the pattern from the sealed class
            arguments = listOf(navArgument(MainAppScreen.EventDetails.EVENT_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString(MainAppScreen.EventDetails.EVENT_ID_ARG)
            if (eventId != null) {
                // Your existing EventDetailsRoute logic remains the same
                EventDetailsRoute(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() },
                    eventRepository = remember { EventRepository() } // Or inject via DI
                )
            } else {
                // Handle case where eventId is null, e.g., show an error screen or navigate back
                // For now, let's just log and pop back
                println("Error: Event ID is missing for EventDetailsRoute")
                navController.popBackStack()
            }
        }




        composable(MainAppScreen.Message.route) {
            MessageScreen(
                userSessionManager = userSessionManager,
                currentScreen = MainAppScreen.Message, // ✅ This is the current tab
                onTabSelected = { screen ->         // ✅ Handle bottom tab navigation
                    navController.navigate(screen.route) {
                        launchSingleTop = true      // Prevent multiple copies
                        restoreState = true         // Restore scroll position, etc.
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true       // Keep previous screen state
                        }
                    }
                },
                onBack = navController::navigateUp,
                onMessageThreadClick = { messageThreadId ->
                    navController.navigate(MainAppScreen.MessageThreadDetails.createRoute(messageThreadId))
                }
            )
            println("Navigated to Message Screen") // Placeholder for MessageScreen
        }

        composable(
            route = MainAppScreen.MessageThreadDetails.ROUTE_PATTERN, // Use the pattern from the sealed class
            arguments = listOf(navArgument(MainAppScreen.MessageThreadDetails.MESSAGETHREAD_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val messageThreadId = backStackEntry.arguments?.getString(MainAppScreen.MessageThreadDetails.MESSAGETHREAD_ID_ARG)
            if (messageThreadId != null) {
                Log.d("MessageThreadID", messageThreadId)
                MessageThreadScreen(
                    messageThreadId = messageThreadId,
                    onBack = { navController.popBackStack() },
                    messageThreadRepository = remember { MessageThreadRepository() },
                    onSendMessage = { message ->},
                )
            } else {
                // Handle case where eventId is null, e.g., show an error screen or navigate back
                // For now, let's just log and pop back
                Log.d("ERROR__","Error: Message ID is missing for ")
                navController.popBackStack()
            }
        }

        composable(MainAppScreen.Finance.route) {
            FinanceScreen(
                userSessionManager = userSessionManager,
                currentScreen = MainAppScreen.Finance, // ✅ This is the current tab
                onTabSelected = { screen ->         // ✅ Handle bottom tab navigation
                    navController.navigate(screen.route) {
                        launchSingleTop = true      // Prevent multiple copies
                        restoreState = true         // Restore scroll position, etc.
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true       // Keep previous screen state
                        }
                    }
                },
                //modifier = TODO()
            )
            println("Navigated to Finance Screen") // Placeholder for FinanceScreen
        }

        // --- Settings Flow ---
        composable(MainAppScreen.Settings.route) {
            SettingsScreen(
                onLogout = {
                    CoroutineScope(Dispatchers.IO).launch {
                        userSessionManager.clearSession()
                    }
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                    }
                },
                onBack = { navController.navigateUp() },
                onViewLinkRequests = { /* TODO */ },
                onNavigateToProfile = { navController.navigate(MainAppScreen.Profile.route) },
                onNavigateToNotifications = { navController.navigate(MainAppScreen.Notifications.route) },
                onNavigateToHelp = { navController.navigate(MainAppScreen.HelpFAQ.route) },
                onNavigateToAbout = { navController.navigate(MainAppScreen.About.route) },
                onDataPrivacy = { navController.navigate(MainAppScreen.DataPrivacy.route) },
                isDarkTheme = isDarkTheme,
                onToggleTheme = { enabled ->
                    themeViewModel.toggleTheme(enabled)
                }
            )
        }

        // --- Individual Settings Sub-Screens ---
        composable(MainAppScreen.Profile.route) {
            // Pass userSessionManager if ProfileScreen needs user data
            ProfileScreen(
                userSessionManager = userSessionManager,
                onBack = navController::navigateUp
            )
        }
        composable(MainAppScreen.Notifications.route) {
            NotificationSettingsScreen(onBack = { navController.navigateUp() })
            println("Navigated to Notifications Screen") // Placeholder for NotificationsScreen
        }

        composable(MainAppScreen.HelpFAQ.route) {
            HelpFAQScreen(onBack = { navController.navigateUp() })
            println("Navigated to Help/FAQ Screen") // Placeholder for HelpFAQScreen
        }
        composable(MainAppScreen.About.route) {
            AboutScreen(onBack = { navController.navigateUp() })
            println("Navigated to About Screen") // Placeholder for AboutScreen
        }
        composable(MainAppScreen.DataPrivacy.route) {
            DataPrivacySettingsScreen(
                onBack = { navController.navigateUp() },
                navController = navController,
                onTermsClick = {
                    navController.navigate(MainAppScreen.Terms.route)
                },
                onPrivacyClick = {
                    navController.navigate(MainAppScreen.PrivacyPolicy.route)
                }
            )
            println("Navigated to Data Privacy Screen") // Placeholder for DataPrivacyScreen
        }


        composable(MainAppScreen.Terms.route) {
            TermsOfServiceScreen(onBack = { navController.navigateUp() })
            println("Navigated to Terms of Service Screen") // Placeholder for TermsOfServiceScreen
        }

        composable(MainAppScreen.PrivacyPolicy.route) {
            TermsOfServiceScreen(onBack = { navController.navigateUp() })
            println("Navigated to Privacy Policy Screen") // Placeholder for PrivacyPolicyScreen
        }

        // --- Academic Sections ---
        composable(MainAppScreen.CoursesList.route) {
            // CoursesListScreen()
            println("Navigated to Courses List Screen") // Placeholder for CoursesListScreen
        }

        composable(MainAppScreen.CourseDetail.ROUTE_PATTERN) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                // TODO: Implement your CourseDetailScreen composable, passing the courseId
                println("Navigated to Course Detail for ID: $courseId") // Placeholder for CourseDetailScreen
            } else {
                println("Error: Course ID missing for CourseDetailScreen")
            }
        }

        composable(MainAppScreen.GradesList.route) {
            // GradesListScreen()
            println("Navigated to Grades List Screen") // Placeholder for GradesListScreen
        }

        composable(MainAppScreen.EvaluationDetail.ROUTE_PATTERN) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("evaluationId")
            if (evaluationId != null) {
                // TODO: Implement your EvaluationDetailScreen composable, passing the evaluationId
                println("Navigated to Evaluation Detail for ID: $evaluationId") // Placeholder for EvaluationDetailScreen
            } else {
                println("Error: Evaluation ID missing for EvaluationDetailScreen")
            }
        }

        // --- User-specific dashboards/lists (for different roles) ---
        composable(MainAppScreen.StudentDashboard.route) {
            // StudentDashboardScreen()
            println("Navigated to Student Dashboard Screen") // Placeholder for StudentDashboardScreen
        }

        composable(MainAppScreen.ParentChildrenList.route) {
            // ParentChildrenListScreen()
            println("Navigated to Parent Children List Screen") // Placeholder for ParentChildrenListScreen
        }

        composable(MainAppScreen.TeacherAssignedCourses.route) {
            // TeacherAssignedCoursesScreen()
            println("Navigated to Teacher Assigned Courses Screen") // Placeholder for TeacherAssignedCoursesScreen
        }
    }
}