package com.example.SIMATS.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object SignUp : Screen("signup")
    data object Login : Screen("login")
    data object ResetPassword : Screen("reset_password")
    data object PrivacyConsent : Screen("privacy_consent")
    data object ProfileSetup : Screen("profile_setup")
    data object DueDate : Screen("due_date")
    data object HealthHistory : Screen("health_history")
    data object GoalsPreferences : Screen("goals_preferences")
    data object Home : Screen("home")
    data object Timeline : Screen("timeline")
    data object BabyDevelopment : Screen("baby_development")
    data object DailyCheckIn : Screen("daily_checkin")
    data object SymptomLogger : Screen("symptom_logger")
}
