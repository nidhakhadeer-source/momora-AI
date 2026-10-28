package com.example.SIMATS.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.ui.duedate.DueDateScreen
import com.example.SIMATS.ui.duedate.DueDateViewModel
import com.example.SIMATS.ui.home.HomeScreen
import com.example.SIMATS.ui.home.HomeViewModel
import com.example.SIMATS.ui.login.LoginScreen
import com.example.SIMATS.ui.login.LoginViewModel
import com.example.SIMATS.ui.onboarding.OnboardingScreen
import com.example.SIMATS.ui.onboarding.OnboardingViewModel
import com.example.SIMATS.ui.profile.ProfileSetupScreen
import com.example.SIMATS.ui.profile.ProfileSetupViewModel
import com.example.SIMATS.ui.signup.SignUpScreen
import com.example.SIMATS.ui.signup.SignUpViewModel
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
                },
                onGetStartedComplete = {
                    currentScreen = Screen.SignUp
                }
            )
        }
        Screen.Login -> {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Onboarding
                },
                onLoginSuccess = {
                    currentScreen = Screen.Home
                },
                onCreateAccountClick = {
                    currentScreen = Screen.SignUp
                }
            )
        }
        Screen.SignUp -> {
            val signUpViewModel: SignUpViewModel = viewModel()
            SignUpScreen(
                viewModel = signUpViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Login
                },
                onNavigateToLogin = {
                    currentScreen = Screen.Login
                },
                onRegistrationSuccess = {
                    currentScreen = Screen.Home
                }
            )
        }
        Screen.Home -> {
            val homeViewModel: HomeViewModel = viewModel()
            HomeScreen(
                viewModel = homeViewModel
            )
        }
        Screen.Profile -> {
            val profileViewModel: ProfileSetupViewModel = viewModel()
            ProfileSetupScreen(
                viewModel = profileViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.Home
                    } else if (index == 1) {
                        currentScreen = Screen.DueDate
                    }
                }
            )
        }
        Screen.DueDate -> {
            val dueDateViewModel: DueDateViewModel = viewModel()
            DueDateScreen(
                viewModel = dueDateViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.Home
                    } else if (index == 4) {
                        currentScreen = Screen.Profile
                    }
                }
            )
        }
    }
}
