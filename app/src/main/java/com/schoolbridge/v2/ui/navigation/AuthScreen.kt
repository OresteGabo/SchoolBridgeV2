
package com.schoolbridge.v2.ui.navigation

/**
 * Defines the sealed class for authentication and initial onboarding routes.
 * These screens are typically shown before a user is authenticated.
 *
 * @param route The unique string identifier for the navigation destination.
 */
sealed class AuthScreen(val route: String) {
    object Onboarding : AuthScreen("onboarding_screen")
    object Login : AuthScreen("login_screen")
    object SignUp : AuthScreen("signup_screen") // Assuming you'll have a sign-up
    object ForgotPassword : AuthScreen("forgot_password_screen") // Assuming a forgot password flow
    object CredentialsSetup : AuthScreen("credentials_setup_screen") // Assuming credentials setup

}