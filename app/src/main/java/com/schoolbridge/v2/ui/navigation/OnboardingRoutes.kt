package com.schoolbridge.v2.ui.navigation

/**
 * Defines the sealed class for all routes within the Onboarding/Authentication flow.
 * These screens are typically visited once (during first launch/registration) or
 * during login/signup/password reset processes. They form a distinct navigation graph.
 */
sealed class OnboardingScreen(val route: String) {
    object Login : OnboardingScreen("login_screen")
    object SignUp : OnboardingScreen("signup_screen")
    object ForgotPassword : OnboardingScreen("forgot_password_screen")

    /**
     * Route for phone verification, requiring a phone number argument.
     * Example usage for navigation: `navController.navigate(OnboardingScreen.Verify.createRoute("123456789"))`
     */
    data class Verify(val phone: String) : OnboardingScreen("verify_screen/{phone}") {
        fun createRoute(phone: String) = "verify_screen/$phone"
    }

    /**
     * Route for setting new credentials (e.g., after successful verification),
     * requiring a phone number argument to pre-populate or associate.
     * Example usage: `navController.navigate(OnboardingScreen.SetCredentials.createRoute("123456789"))`
     */
    data class SetCredentials(val phone: String) : OnboardingScreen("set_credentials_screen/{phone}") {
        fun createRoute(phone: String) = "set_credentials_screen/$phone"
    }

    object TermsOfService : OnboardingScreen("terms_of_service_screen")
    object PrivacyPolicy : OnboardingScreen("privacy_policy_screen") // For initial consent during onboarding

    /**
     * Route for completing the user's initial profile setup after basic registration.
     */
    object CompleteProfile : OnboardingScreen("complete_profile_screen")

    /**
     * Route for parents to link their children's accounts during the onboarding process.
     */
    object LinkChildren : OnboardingScreen("link_children_screen")

    /**
     * Route for parents to preview or confirm the linked children during onboarding.
     */
    object PreviewChildren : OnboardingScreen("preview_children_screen")
}