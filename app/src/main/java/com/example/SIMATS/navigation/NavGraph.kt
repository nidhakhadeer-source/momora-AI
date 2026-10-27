package com.example.SIMATS.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.SIMATS.ui.ai.AiChatScreen
import com.example.SIMATS.ui.babydevelopment.BabyDevelopmentScreen
import com.example.SIMATS.ui.bloodpressure.BloodPressureScreen
import com.example.SIMATS.ui.checkin.DailyCheckInScreen
import com.example.SIMATS.ui.contraction.ContractionTimerScreen
import com.example.SIMATS.ui.dailyplan.DailyPlanScreen
import com.example.SIMATS.ui.duedate.DueDateScreen
import com.example.SIMATS.ui.glucose.GlucoseLogScreen
import com.example.SIMATS.ui.goals.GoalsPreferencesScreen
import com.example.SIMATS.ui.healthhistory.HealthHistoryScreen
import com.example.SIMATS.ui.home.HomeScreen
import com.example.SIMATS.ui.hydration.HydrationLogScreen
import com.example.SIMATS.ui.kickcounter.KickCounterScreen
import com.example.SIMATS.ui.kickhistory.KickHistoryScreen
import com.example.SIMATS.ui.login.LoginScreen
import com.example.SIMATS.ui.privacyconsent.PrivacyConsentScreen
import com.example.SIMATS.ui.profile.ProfileSetupScreen
import com.example.SIMATS.ui.resetpassword.ResetPasswordScreen
import com.example.SIMATS.ui.signup.SignUpScreen
import com.example.SIMATS.ui.splash.SplashScreen
import com.example.SIMATS.ui.symptom.SymptomLoggerScreen
import com.example.SIMATS.ui.symptominsights.SymptomInsightsScreen
import com.example.SIMATS.ui.timeline.TimelineScreen
import com.example.SIMATS.ui.weight.WeightTrackerScreen

@Composable
fun AppNavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    when (currentScreen) {
        Screen.Splash -> {
            SplashScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.SignUp.route -> currentScreen = Screen.SignUp
                        Screen.Login.route -> currentScreen = Screen.Login
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                }
            )
        }
        Screen.SignUp -> {
            SignUpScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.PrivacyConsent.route -> currentScreen = Screen.PrivacyConsent
                        Screen.Login.route -> currentScreen = Screen.Login
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.Splash
                }
            )
        }
        Screen.Login -> {
            LoginScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.PrivacyConsent.route -> currentScreen = Screen.PrivacyConsent
                        Screen.SignUp.route -> currentScreen = Screen.SignUp
                        Screen.ResetPassword.route -> currentScreen = Screen.ResetPassword
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.SignUp
                }
            )
        }
        Screen.ResetPassword -> {
            ResetPasswordScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.Login.route -> currentScreen = Screen.Login
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.Login
                }
            )
        }
        Screen.PrivacyConsent -> {
            PrivacyConsentScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.ProfileSetup.route -> currentScreen = Screen.ProfileSetup
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.Login
                }
            )
        }
        Screen.ProfileSetup -> {
            ProfileSetupScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.DueDate.route -> currentScreen = Screen.DueDate
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.PrivacyConsent
                }
            )
        }
        Screen.DueDate -> {
            DueDateScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.HealthHistory.route -> currentScreen = Screen.HealthHistory
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.ProfileSetup
                }
            )
        }
        Screen.HealthHistory -> {
            HealthHistoryScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.GoalsPreferences.route -> currentScreen = Screen.GoalsPreferences
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.DueDate
                }
            )
        }
        Screen.GoalsPreferences -> {
            GoalsPreferencesScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.HealthHistory
                }
            )
        }
        Screen.Home -> {
            HomeScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.Timeline.route -> currentScreen = Screen.Timeline
                        Screen.DailyCheckIn.route -> currentScreen = Screen.DailyCheckIn
                        Screen.DailyPlan.route -> currentScreen = Screen.DailyPlan
                        Screen.AiChat.route -> currentScreen = Screen.AiChat
                        Screen.Splash.route -> currentScreen = Screen.Splash
                    }
                }
            )
        }
        Screen.Timeline -> {
            TimelineScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.BabyDevelopment.route -> currentScreen = Screen.BabyDevelopment
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.Home
                }
            )
        }
        Screen.BabyDevelopment -> {
            BabyDevelopmentScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.Timeline
                }
            )
        }
        Screen.DailyCheckIn -> {
            DailyCheckInScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.SymptomLogger.route -> currentScreen = Screen.SymptomLogger
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.Home
                }
            )
        }
        Screen.SymptomLogger -> {
            SymptomLoggerScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.SymptomInsights.route -> currentScreen = Screen.SymptomInsights
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.DailyCheckIn
                }
            )
        }
        Screen.SymptomInsights -> {
            SymptomInsightsScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.AiChat.route -> currentScreen = Screen.AiChat
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.SymptomLogger
                }
            )
        }
        Screen.AiChat -> {
            AiChatScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.SymptomInsights
                }
            )
        }
        Screen.DailyPlan -> {
            DailyPlanScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.KickCounter.route -> currentScreen = Screen.KickCounter
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.Home
                }
            )
        }
        Screen.KickCounter -> {
            KickCounterScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.KickHistory.route -> currentScreen = Screen.KickHistory
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.DailyPlan
                }
            )
        }
        Screen.KickHistory -> {
            KickHistoryScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.ContractionTimer.route -> currentScreen = Screen.ContractionTimer
                        Screen.KickCounter.route -> currentScreen = Screen.KickCounter
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.KickCounter
                }
            )
        }
        Screen.ContractionTimer -> {
            ContractionTimerScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.WeightTracker.route -> currentScreen = Screen.WeightTracker
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.KickHistory
                }
            )
        }
        Screen.WeightTracker -> {
            WeightTrackerScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.BloodPressure.route -> currentScreen = Screen.BloodPressure
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.ContractionTimer
                }
            )
        }
        Screen.BloodPressure -> {
            BloodPressureScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.GlucoseLog.route -> currentScreen = Screen.GlucoseLog
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.WeightTracker
                }
            )
        }
        Screen.GlucoseLog -> {
            GlucoseLogScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.HydrationLog.route -> currentScreen = Screen.HydrationLog
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.BloodPressure
                }
            )
        }
        Screen.HydrationLog -> {
            HydrationLogScreen(
                onNavigateTo = { route ->
                    when (route) {
                        Screen.Home.route -> currentScreen = Screen.Home
                    }
                },
                onNavigateBack = {
                    currentScreen = Screen.GlucoseLog
                }
            )
        }
    }
}
