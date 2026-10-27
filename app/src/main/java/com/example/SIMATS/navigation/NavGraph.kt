package com.example.SIMATS.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.SIMATS.ui.duedate.DueDateScreen
import com.example.SIMATS.ui.goals.GoalsPreferencesScreen
import com.example.SIMATS.ui.healthhistory.HealthHistoryScreen
import com.example.SIMATS.ui.login.LoginScreen
import com.example.SIMATS.ui.privacyconsent.PrivacyConsentScreen
import com.example.SIMATS.ui.profile.ProfileSetupScreen
import com.example.SIMATS.ui.resetpassword.ResetPasswordScreen
import com.example.SIMATS.ui.signup.SignUpScreen
import com.example.SIMATS.ui.splash.SplashScreen
import com.example.SIMATS.ui.theme.MomoraScreenBackground
import com.example.SIMATS.ui.theme.MomoraTealPrimary

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
            PlaceholderScreen(title = "Home Screen")
        }
    }
}

@Composable
private fun PlaceholderScreen(title: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MomoraScreenBackground),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = MomoraTealPrimary
        )
    }
}
