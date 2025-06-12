// src/main/java/com/schoolbridge/v2/ui/navigation/AppNavHost.kt
package com.schoolbridge.v2.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// Import other screen composables as you create them
import com.schoolbridge.v2.ui.onboarding.OnboardingScreen
import com.schoolbridge.v2.ui.message.MessageScreen
import com.schoolbridge.v2.ui.finance.FinanceScreen
import com.schoolbridge.v2.ui.home.HomeScreen
import com.schoolbridge.v2.ui.onboarding.auth.LoginScreen
import com.schoolbridge.v2.ui.settings.profile.ProfileScreen
// etc.

/**
 * The main navigation host for the SchoolBridge V2 application.
 * This composable defines all the navigation routes and their associated UI screens.
 *
 * @param navController The [NavHostController] instance that manages navigation within the host.
 * If null, a default one is remembered.
 * @param startDestination The route to the initial screen when the app starts.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String, // This will typically be AuthScreen.Onboarding.route or AuthScreen.Login.route
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Authentication/Onboarding Flow ---
        composable(AuthScreen.Onboarding.route) {
            // TODO: Implement your OnboardingScreen composable
            // OnboardingScreen(onFinishOnboarding = { navController.navigate(AuthScreen.Login.route) })
            println("Navigated to Onboarding Screen") // Placeholder
        }

        composable(AuthScreen.Login.route) {
            LoginScreen(
                navigateToHome = {
                    // After successful login, navigate to the main app flow
                    // and clear the back stack so the user can't go back to login
                    navController.navigate(MainAppScreen.Home.route) {
                        popUpTo(AuthScreen.Login.route) {
                            inclusive = true // Remove login from back stack
                        }
                        // If you have an onboarding, you might want to pop up to the start destination
                        // or even clear the entire back stack depending on your flow.
                        // popUpTo(navController.graph.findStartDestination().id) { inclusive = true }
                    }
                }
                // viewModel parameter will be provided by default or your custom factory
            )
        }

        composable(AuthScreen.SignUp.route) {
            // TODO: Implement your SignUpScreen composable
            // SignUpScreen(onSignUpSuccess = { navController.navigate(AuthScreen.Login.route) })
            println("Navigated to Sign Up Screen") // Placeholder
        }

        composable(AuthScreen.ForgotPassword.route) {
            // TODO: Implement your ForgotPasswordScreen composable
            println("Navigated to Forgot Password Screen") // Placeholder
        }

        // --- Main Application Flow (Post-Login) ---
        // These use the routes defined in MainAppScreen
        composable(MainAppScreen.Home.route) {
            HomeScreen() // Your actual Home Screen composable
        }

        composable(MainAppScreen.Message.route) {
            // TODO: Implement your MessageScreen composable
             MessageScreen()
            println("Navigated to Message Screen") // Placeholder
        }

        composable(MainAppScreen.Finance.route) {
            // TODO: Implement your FinanceScreen composable
             FinanceScreen()
            println("Navigated to Finance Screen") // Placeholder
        }

        composable(MainAppScreen.Profile.route) {
            // TODO: Implement your ProfileScreen composable
            println("Navigated to Profile Screen") // Placeholder
        }

        composable(MainAppScreen.Notifications.route) {
            // TODO: Implement your NotificationsScreen composable
            println("Navigated to Notifications Screen") // Placeholder
        }

        composable(MainAppScreen.Settings.route) {
            // TODO: Implement your SettingsScreen composable
            println("Navigated to Settings Screen") // Placeholder
        }

        composable(MainAppScreen.Language.route) {
            // TODO: Implement your LanguageSettingScreen composable
            println("Navigated to Language Setting Screen") // Placeholder
        }

        composable(MainAppScreen.Theme.route) {
            // TODO: Implement your ThemeSettingScreen composable
            println("Navigated to Theme Setting Screen") // Placeholder
        }

        composable(MainAppScreen.HelpFAQ.route) {
            // TODO: Implement your HelpFAQScreen composable
            println("Navigated to Help/FAQ Screen") // Placeholder
        }

        composable(MainAppScreen.About.route) {
            // TODO: Implement your AboutScreen composable
            println("Navigated to About Screen") // Placeholder
        }
// **FIXED LINES HERE:**
        composable(MainAppScreen.CourseDetail.ROUTE_PATTERN) { backStackEntry ->
            val courseId = backStackEntry.arguments?.getString("courseId")
            if (courseId != null) {
                // TODO: Implement your CourseDetailScreen composable, passing the courseId
                println("Navigated to Course Detail for ID: $courseId")
            } else {
                println("Error: Course ID missing for CourseDetailScreen")
            }
        }

        // **FIXED LINES HERE:**
        composable(MainAppScreen.EvaluationDetail.ROUTE_PATTERN) { backStackEntry ->
            val evaluationId = backStackEntry.arguments?.getString("evaluationId")
            if (evaluationId != null) {
                // TODO: Implement your EvaluationDetailScreen composable, passing the evaluationId
                println("Navigated to Evaluation Detail for ID: $evaluationId")
            } else {
                println("Error: Evaluation ID missing for EvaluationDetailScreen")
            }
        }

        composable(MainAppScreen.DataPrivacy.route) {
            // TODO: Implement your DataPrivacyScreen composable
            println("Navigated to Data Privacy Screen") // Placeholder
        }

        composable(MainAppScreen.CoursesList.route) {
            // TODO: Implement your CoursesListScreen composable
            println("Navigated to Courses List Screen") // Placeholder
        }

        composable(MainAppScreen.GradesList.route) {
            // TODO: Implement your GradesListScreen composable
            println("Navigated to Grades List Screen") // Placeholder
        }

        composable(MainAppScreen.StudentDashboard.route) {
            // TODO: Implement your StudentDashboardScreen composable
            println("Navigated to Student Dashboard Screen") // Placeholder
        }

        composable(MainAppScreen.ParentChildrenList.route) {
            // TODO: Implement your ParentChildrenListScreen composable
            println("Navigated to Parent Children List Screen") // Placeholder
        }

        composable(MainAppScreen.TeacherAssignedCourses.route) {
            // TODO: Implement your TeacherAssignedCoursesScreen composable
            println("Navigated to Teacher Assigned Courses Screen") // Placeholder
        }
    }
}

