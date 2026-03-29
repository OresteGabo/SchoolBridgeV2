// src/main/java/com/schoolbridge/v2/ui/navigation/AppNavHost.kt
package com.schoolbridge.v2.ui.navigation

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.navigation.NavBackStackEntry
import androidx.navigation.navArgument
import com.schoolbridge.v2.components.CustomBottomNavBar
import com.schoolbridge.v2.data.dto.user.RoleRequestDto
import com.schoolbridge.v2.data.remote.RoleLookupApiServiceImpl
import com.schoolbridge.v2.data.remote.RoleRequestApiServiceImpl
import com.schoolbridge.v2.ui.event.EventDetailsRoute
import com.schoolbridge.v2.ui.event.EventRepository
import com.schoolbridge.v2.ui.onboarding.auth.CredentialsSetupScreen
import com.schoolbridge.v2.ui.onboarding.auth.ForgotPasswordScreen
import com.schoolbridge.v2.ui.onboarding.auth.SignUpScreen
import com.schoolbridge.v2.ui.onboarding.OnboardingScreen
import com.schoolbridge.v2.ui.onboarding.legal.TermsOfServiceScreen
import com.schoolbridge.v2.ui.settings.about.AboutScreen
import com.schoolbridge.v2.ui.settings.dataprivacy.DataPrivacySettingsScreen
import com.schoolbridge.v2.ui.settings.help.HelpFAQScreen
import com.schoolbridge.v2.ui.settings.notifications.NotificationSettingsScreen
import com.schoolbridge.v2.ui.theme.ThemeViewModel
import androidx.compose.runtime.getValue
import com.schoolbridge.v2.domain.user.UserRole
import com.schoolbridge.v2.ui.home.ParentRoleRequestScreen
import com.schoolbridge.v2.ui.home.RequestRoleScreen
import com.schoolbridge.v2.ui.academic.AcademicPlaceholderDetailScreen
import com.schoolbridge.v2.ui.academic.CoursesListScreen
import com.schoolbridge.v2.ui.academic.GradesListScreen
import com.schoolbridge.v2.ui.home.SchoolAdminRoleRequestScreen
import com.schoolbridge.v2.ui.home.StudentRoleRequestScreen
import com.schoolbridge.v2.ui.home.TeacherRoleRequestScreen
import com.schoolbridge.v2.ui.home.alert.AlertsScreen
import com.schoolbridge.v2.ui.home.alert.EventsScreen
import com.schoolbridge.v2.ui.home.timetable.TimetableTabsScreen
import com.schoolbridge.v2.ui.message.MessageScreen
import com.schoolbridge.v2.ui.message.MessageConversationScreen
import com.schoolbridge.v2.ui.onboarding.auth.VerificationScreen
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
    themeViewModel: ThemeViewModel,
    pendingMessageConversationId: String? = null,
    pendingCallMessageId: String? = null,
    openScheduleRequested: Boolean = false,
    onPendingNotificationConsumed: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDarkTheme by themeViewModel.isDark.collectAsState()
    val palette by themeViewModel.palette.collectAsState()
    val contrast by themeViewModel.contrast.collectAsState()
    val currentUser = userSessionManager.currentUser
    val roleRequestApiService = remember(userSessionManager) { RoleRequestApiServiceImpl(userSessionManager) }
    val roleLookupApiService = remember(userSessionManager) { RoleLookupApiServiceImpl(userSessionManager) }

    LaunchedEffect(pendingMessageConversationId, pendingCallMessageId, openScheduleRequested) {
        when {
            pendingMessageConversationId != null -> {
                navController.navigate(MainAppScreen.Message.route) {
                    launchSingleTop = true
                }
                navController.navigate(
                    MainAppScreen.MessageConversationDetails.createRoute(
                        messageConversationId = pendingMessageConversationId,
                        callMessageId = pendingCallMessageId
                    )
                )
                onPendingNotificationConsumed()
            }
            openScheduleRequested -> {
                navController.navigate(MainAppScreen.WeeklySchedule.route)
                onPendingNotificationConsumed()
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // --- Authentication/Onboarding Flow ---
        composable(AuthScreen.Onboarding.route) {
            OnboardingScreen(
                onFinished = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.Onboarding.route) { inclusive = true }
                    }
                }
            )
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
        }

        composable(AuthScreen.CredentialsSetup.route) {
            CredentialsSetupScreen(
                onContinue = { phoneNumber ->
                    navController.navigate("${MainAppScreen.VerificationScreen.route}/$phoneNumber")
                },
                modifier = Modifier
            )
        }

        composable(
            route = "${MainAppScreen.VerificationScreen.route}/{phoneNumber}",
            arguments = listOf(navArgument("phoneNumber") { defaultValue = "" })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""

            VerificationScreen(
                phoneNumber = phoneNumber,
                onVerificationSuccess = {
                    navController.navigate(MainAppScreen.Home.route) {
                        popUpTo(AuthScreen.CredentialsSetup.route) { inclusive = true }
                    }
                },
                onResendCode = {}
            )
        }

        composable(AuthScreen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.navigateUp() },
                onBackToLogin = {
                    navController.navigate(AuthScreen.Login.route) {
                        popUpTo(AuthScreen.ForgotPassword.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }


        composable(MainAppScreen.Home.route) {
            HomeRoute(
                userSessionManager = userSessionManager,
                onSettingsClick = {
                    navController.navigate(MainAppScreen.Settings.route)
                },
                onEventClick = { eventId ->
                    navController.navigate(MainAppScreen.EventDetails.createRoute(eventId))
                },
                modifier = modifier,
                onViewAllAlertsClick = {
                    navController.navigate(MainAppScreen.Alerts.route)
                },
                onViewAllEventsClick = {
                    navController.navigate(MainAppScreen.Events.route)
                },
                currentScreen = MainAppScreen.Home,
                onTabSelected = navController::navigateToMainScreen,
                onWeeklyViewClick = {
                    navController.navigate(MainAppScreen.WeeklySchedule.route)
                },
                onRequestNewRole = {
                    navController.navigate(MainAppScreen.RequestRole.route)
                }
            )
        }
        composable(MainAppScreen.RequestRole.route) {
            val currentUser by userSessionManager.currentUser.collectAsState() // collect state properly

            RequestRoleScreen(
                activeRoles = currentUser?.activeRoles ?: emptySet(),          // safe fallback
                linkedStudentNames = currentUser?.linkedStudents?.map { "${it.firstName} ${it.lastName}".trim() } ?: emptyList(),
                onRoleSelected = { role ->
                    when(role){
                        UserRole.PARENT -> navController.navigate(MainAppScreen.RequestParentRole.route)
                        UserRole.STUDENT -> navController.navigate(MainAppScreen.RequestStudentRole.route)
                        UserRole.TEACHER -> navController.navigate(MainAppScreen.RequestTeacherRole.route)
                        UserRole.SCHOOL_ADMIN -> navController.navigate(MainAppScreen.RequestSchoolAdminRole.route)
                        UserRole.GUEST -> {}
                    }
                    // TODO: handle selected role request
                },
                onBack = { navController.navigateUp() }
            )
        }
        composable(MainAppScreen.RequestStudentRole.route) {
            val currentUser by userSessionManager.currentUser.collectAsState()
            StudentRoleRequestScreen(
                alreadyHasRole = currentUser?.activeRoles?.contains(UserRole.STUDENT) == true,
                onSearchSchools = { query -> roleLookupApiService.searchSchools(query) },
                onSubmit = { school, level, dob ->
                    roleRequestApiService.submitRoleRequest(
                        RoleRequestDto(
                            requestedRole = UserRole.STUDENT.name,
                            schoolId = school.id,
                            schoolName = school.name,
                            justification = if (level.isBlank()) null else "Student access request for $level",
                            supportingDocumentsUrls = emptyList(),
                            childStudentId = null,
                            childNationalId = null,
                            parentNationalId = null,
                            familyCardDocumentUrl = null,
                            academicLevel = level,
                            childDateOfBirth = dob
                        )
                    )
                    navController.navigateUp()
                },
                onCancel = {
                    navController.navigateUp()
                }
            )
        }

        composable(MainAppScreen.RequestParentRole.route) {
            val currentUser by userSessionManager.currentUser.collectAsState()
            ParentRoleRequestScreen(
                alreadyHasRole = currentUser?.activeRoles?.contains(UserRole.PARENT) == true,
                linkedStudentNames = currentUser?.linkedStudents?.map { "${it.firstName} ${it.lastName}".trim() } ?: emptyList(),
                onSearchStudents = { query, schoolId -> roleLookupApiService.searchStudents(query, schoolId) },
                onSubmit = { student, relationship ->
                    roleRequestApiService.submitRoleRequest(
                        RoleRequestDto(
                            requestedRole = UserRole.PARENT.name,
                            schoolId = student.schoolId,
                            schoolName = student.schoolName,
                            studentUserId = student.studentUserId,
                            justification = "Parent link request for ${student.fullName}",
                            supportingDocumentsUrls = emptyList(),
                            childStudentId = student.studentUserId.toString(),
                            childNationalId = null,
                            parentNationalId = null,
                            familyCardDocumentUrl = null,
                            childName = student.fullName,
                            relationshipLabel = relationship.label
                        )
                    )
                    navController.navigateUp()
                },
                onCancel = {
                    navController.navigateUp()
                }
            )
        }

        composable(MainAppScreen.RequestTeacherRole.route) {
            val currentUser by userSessionManager.currentUser.collectAsState()
            TeacherRoleRequestScreen(
                alreadyHasRole = currentUser?.activeRoles?.contains(UserRole.TEACHER) == true,
                onSearchSchools = { query -> roleLookupApiService.searchSchools(query) },
                onSubmit = { school, message ->
                    roleRequestApiService.submitRoleRequest(
                        RoleRequestDto(
                            requestedRole = UserRole.TEACHER.name,
                            schoolId = school.id,
                            schoolName = school.name,
                            justification = if (message.isBlank()) null else "Teacher access request",
                            supportingDocumentsUrls = emptyList(),
                            childStudentId = null,
                            childNationalId = null,
                            parentNationalId = null,
                            familyCardDocumentUrl = null,
                            roleDescription = message
                        )
                    )
                    navController.navigateUp()
                },
                onCancel = {
                    navController.navigateUp()
                }
            )
        }

        composable(MainAppScreen.RequestSchoolAdminRole.route) {
            val currentUser by userSessionManager.currentUser.collectAsState()
            SchoolAdminRoleRequestScreen(
                alreadyHasRole = currentUser?.activeRoles?.contains(UserRole.SCHOOL_ADMIN) == true,
                onSearchSchools = { query -> roleLookupApiService.searchSchools(query) },
                onSubmit = { school, responsibility ->
                    roleRequestApiService.submitRoleRequest(
                        RoleRequestDto(
                            requestedRole = UserRole.SCHOOL_ADMIN.name,
                            schoolId = school.id,
                            schoolName = school.name,
                            justification = if (responsibility.isBlank()) null else "School admin access request",
                            supportingDocumentsUrls = emptyList(),
                            childStudentId = null,
                            childNationalId = null,
                            parentNationalId = null,
                            familyCardDocumentUrl = null,
                            responsibilityScope = responsibility
                        )
                    )
                    navController.navigateUp()
                },
                onCancel = {
                    navController.navigateUp()
                }
            )
        }

        composable(MainAppScreen.WeeklySchedule.route){
            val currentUser by userSessionManager.currentUser.collectAsState(initial = null)
            TimetableTabsScreen(
                userSessionManager = userSessionManager,
                currentScreen = MainAppScreen.WeeklySchedule,
                onTabSelected = navController::navigateToMainScreen,
                currentUser = currentUser,
                onBack = null,
                onOpenMessageConversation = { messageConversationId, callMessageId ->
                    navController.navigate(
                        MainAppScreen.MessageConversationDetails.createRoute(
                            messageConversationId = messageConversationId,
                            callMessageId = callMessageId
                        )
                    )
                }
            )
        }

        composable(MainAppScreen.Alerts.route){
            val currentUser by userSessionManager.currentUser.collectAsState(initial = null)
            AlertsScreen(
                userSessionManager = userSessionManager,
                currentScreen = MainAppScreen.Alerts,
                onTabSelected = navController::navigateToMainScreen,
                currentUser = currentUser,
                onBack = null,
            )
        }
        composable(MainAppScreen.Events.route){
            EventsScreen(
                onBack = { navController.navigateUp() },
                onEventClick = { eventId ->
                    navController.navigate(MainAppScreen.EventDetails.createRoute(eventId))
                }
            )
        }


        composable(
            route = MainAppScreen.EventDetails.ROUTE_PATTERN,
            arguments = listOf(navArgument(MainAppScreen.EventDetails.EVENT_ID_ARG) {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val eventId = backStackEntry.requiredStringArg(
                navController = navController,
                argName = MainAppScreen.EventDetails.EVENT_ID_ARG,
                screenName = "EventDetails"
            )
            if (eventId != null) {
                EventDetailsRoute(
                    eventId = eventId,
                    onBackClick = { navController.popBackStack() },
                    eventRepository = remember { EventRepository() }
                )
            }
        }

        composable(MainAppScreen.Message.route) {
            MessageScreen(
                userSessionManager = userSessionManager,
                currentScreen = MainAppScreen.Message,
                onTabSelected = navController::navigateToMainScreen,
                onBack = navController::navigateUp,
                onMessageConversationClick = { messageConversationId ->
                    navController.navigate(
                        MainAppScreen.MessageConversationDetails.createRoute(
                            messageConversationId
                        )
                    )
                }
            )
        }

        composable(
            route = MainAppScreen.MessageConversationDetails.ROUTE_PATTERN,
            arguments = listOf(navArgument(MainAppScreen.MessageConversationDetails.MESSAGE_CONVERSATION_ID_ARG) {
                type = NavType.StringType
            }, navArgument(MainAppScreen.MessageConversationDetails.CALL_MESSAGE_ID_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val messageConversationId = backStackEntry.requiredStringArg(
                navController = navController,
                argName = MainAppScreen.MessageConversationDetails.MESSAGE_CONVERSATION_ID_ARG,
                screenName = "MessageConversationDetails"
            )
            val callMessageId = backStackEntry.arguments?.getString(
                MainAppScreen.MessageConversationDetails.CALL_MESSAGE_ID_ARG
            )
            if (messageConversationId != null) {
                Log.d("MessageConversationID", messageConversationId)
                MessageConversationScreen(
                    userSessionManager = userSessionManager,
                    initialConversationId = messageConversationId,
                    initialCallMessageId = callMessageId,
                    onBack = { navController.popBackStack() }
                )
            }
        }

        composable(MainAppScreen.Finance.route) {
            FinanceScreen(
                userSessionManager = userSessionManager,
                currentScreen = MainAppScreen.Finance,
                onTabSelected = navController::navigateToMainScreen
            )
        }

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
                onBack = navController::navigateUp,
                onViewLinkRequests = { /* TODO */ },
                onNavigateToProfile        = { navController.navigate(MainAppScreen.Profile.route) },
                onNavigateToNotifications  = { navController.navigate(MainAppScreen.Notifications.route) },
                onNavigateToHelp           = { navController.navigate(MainAppScreen.HelpFAQ.route) },
                onNavigateToAbout          = { navController.navigate(MainAppScreen.About.route) },
                onDataPrivacy              = { navController.navigate(MainAppScreen.DataPrivacy.route) },
                isDarkTheme    = isDarkTheme,
                currentPalette = palette,
                currentContrast = contrast,
                onToggleTheme   = themeViewModel::toggleDark,
                onPalettePicked = { newPalette ->
                    themeViewModel.setPalette(newPalette)
                },
                onContrastPicked = { newContrast ->
                    themeViewModel.setContrast(newContrast)
                }
            )
        }


        composable(MainAppScreen.Profile.route) {
            ProfileScreen(
                userSessionManager = userSessionManager,
                onBack = navController::navigateUp
            )
        }
        composable(MainAppScreen.Notifications.route) {
            NotificationSettingsScreen(onBack = { navController.navigateUp() })
        }

        composable(MainAppScreen.HelpFAQ.route) {
            HelpFAQScreen(onBack = { navController.navigateUp() })
        }
        composable(MainAppScreen.About.route) {
            AboutScreen(onBack = { navController.navigateUp() })
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
        }


        composable(MainAppScreen.Terms.route) {
            TermsOfServiceScreen(onBack = { navController.navigateUp() })
        }

        composable(MainAppScreen.PrivacyPolicy.route) {
            TermsOfServiceScreen(onBack = { navController.navigateUp() })
        }

        composable(MainAppScreen.CoursesList.route) {
            CoursesListScreen(
                userSessionManager = userSessionManager,
                onBack = { navController.navigateUp() }
            )
        }

        composable(MainAppScreen.CourseDetail.ROUTE_PATTERN) { backStackEntry ->
            val courseId = backStackEntry.requiredStringArg(
                navController = navController,
                argName = MainAppScreen.CourseDetail.COURSE_ID_ARG,
                screenName = "CourseDetail"
            )
            if (courseId != null) {
                AcademicPlaceholderDetailScreen(
                    title = "Course details",
                    body = "Course detail is not fully wired yet, but this route is now safe and ready for the next content pass. The selected course id is $courseId.",
                    onBack = { navController.navigateUp() }
                )
            }
        }

        composable(MainAppScreen.GradesList.route) {
            GradesListScreen(onBack = { navController.navigateUp() })
        }

        composable(MainAppScreen.EvaluationDetail.ROUTE_PATTERN) { backStackEntry ->
            val evaluationId = backStackEntry.requiredStringArg(
                navController = navController,
                argName = MainAppScreen.EvaluationDetail.EVALUATION_ID_ARG,
                screenName = "EvaluationDetail"
            )
            if (evaluationId != null) {
                AcademicPlaceholderDetailScreen(
                    title = "Evaluation details",
                    body = "Evaluation detail is still waiting for backend-linked content, but this route now opens safely instead of landing in a TODO hole. The selected evaluation id is $evaluationId.",
                    onBack = { navController.navigateUp() }
                )
            }
        }

        composable(MainAppScreen.StudentDashboard.route) {
            AcademicPlaceholderDetailScreen(
                title = "Student dashboard",
                body = "This dashboard route is reserved for a fuller academic overview, but it now has a real destination instead of an empty stub.",
                onBack = { navController.navigateUp() }
            )
        }

        composable(MainAppScreen.ParentChildrenList.route) {
            AcademicPlaceholderDetailScreen(
                title = "Linked children",
                body = "This route is ready for the future family management view. For now it opens safely with a real screen instead of a blank placeholder.",
                onBack = { navController.navigateUp() }
            )
        }

        composable(MainAppScreen.TeacherAssignedCourses.route) {
            AcademicPlaceholderDetailScreen(
                title = "Assigned courses",
                body = "This route is prepared for the teacher workload view. It no longer drops into an empty navigation placeholder.",
                onBack = { navController.navigateUp() }
            )
        }
    }
}

private fun NavHostController.navigateToMainScreen(screen: MainAppScreen) {
    navigate(screen.route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.startDestinationId) {
            saveState = true
        }
    }
}

private fun NavBackStackEntry.requiredStringArg(
    navController: NavHostController,
    argName: String,
    screenName: String
): String? {
    val value = arguments?.getString(argName)
    if (value == null) {
        Log.w("AppNavHost", "Missing required nav argument '$argName' for $screenName")
        navController.popBackStack()
    }
    return value
}
