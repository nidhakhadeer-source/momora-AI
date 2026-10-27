package com.example.SIMATS.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object SignUp : Screen("signup")
    data object Login : Screen("login")
    data object ResetPassword : Screen("reset_password")
    data object PrivacyConsent : Screen("privacy_consent")
    data object ProfileSetup : Screen("profile_setup")
    data object Home : Screen("home")
}
