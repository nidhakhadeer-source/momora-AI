package com.example.SIMATS.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash")
    data object Onboarding : Screen("onboarding")
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object Home : Screen("home")
    data object Profile : Screen("profile")
    data object DueDate : Screen("due_date")
    data object HealthReport : Screen("health_report")
    data object WeightTracker : Screen("weight_tracker")
    data object BloodPressure : Screen("blood_pressure")
    data object BabyDevelopment : Screen("baby_development")
    data object AiChat : Screen("ai_chat")
    data object Symptom : Screen("symptom")
    data object Nutrition : Screen("nutrition")
    data object Medication : Screen("medication")
}
