package com.example.SIMATS.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.ui.onboarding.OnboardingScreen
import com.example.SIMATS.ui.onboarding.OnboardingViewModel
import com.example.SIMATS.ui.splash.SplashScreen
import com.example.SIMATS.ui.splash.SplashViewModel

@Composable
fun AppNavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }

    when (currentScreen) {
        Screen.Splash -> {
            val splashViewModel: SplashViewModel = viewModel()
            SplashScreen(
                viewModel = splashViewModel,
                onNavigateNext = {
                    currentScreen = Screen.Onboarding
                }
            )
        }
        Screen.Onboarding -> {
            val onboardingViewModel: OnboardingViewModel = viewModel()
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                }
            )
        }
        else -> {
            val onboardingViewModel: OnboardingViewModel = viewModel()
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                }
            )
        }
    }
}
