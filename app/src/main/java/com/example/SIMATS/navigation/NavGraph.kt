package com.example.SIMATS.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.ui.ai.AiChatScreen
import com.example.SIMATS.ui.ai.AiChatViewModel
import com.example.SIMATS.ui.babydevelopment.BabyDevelopmentScreen
import com.example.SIMATS.ui.babydevelopment.BabyDevelopmentViewModel
import com.example.SIMATS.ui.bloodpressure.BloodPressureScreen
import com.example.SIMATS.ui.bloodpressure.BloodPressureViewModel
import com.example.SIMATS.ui.calendar.AppointmentsCalendarScreen
import com.example.SIMATS.ui.calendar.AppointmentsCalendarViewModel
import com.example.SIMATS.ui.duedate.DueDateScreen
import com.example.SIMATS.ui.duedate.DueDateViewModel
import com.example.SIMATS.ui.healthreport.HealthReportScreen
import com.example.SIMATS.ui.healthreport.HealthReportViewModel
import com.example.SIMATS.ui.home.HomeScreen
import com.example.SIMATS.ui.home.HomeViewModel
import com.example.SIMATS.ui.login.LoginScreen
import com.example.SIMATS.ui.login.LoginViewModel
import com.example.SIMATS.ui.main.MainScreen
import com.example.SIMATS.ui.main.MainViewModel
import com.example.SIMATS.ui.medicalreports.MedicalReportsScreen
import com.example.SIMATS.ui.medicalreports.MedicalReportsViewModel
import com.example.SIMATS.ui.medication.MedicationRemindersScreen
import com.example.SIMATS.ui.medication.MedicationRemindersViewModel
import com.example.SIMATS.ui.notifications.NotificationsScreen
import com.example.SIMATS.ui.notifications.NotificationsViewModel
import com.example.SIMATS.ui.nutrition.NutritionPlanScreen
import com.example.SIMATS.ui.nutrition.NutritionPlanViewModel
import com.example.SIMATS.ui.onboarding.OnboardingScreen
import com.example.SIMATS.ui.onboarding.OnboardingViewModel
import com.example.SIMATS.ui.profile.ProfileSetupScreen
import com.example.SIMATS.ui.profile.ProfileSetupViewModel
import com.example.SIMATS.ui.signup.SignUpScreen
import com.example.SIMATS.ui.signup.SignUpViewModel
import com.example.SIMATS.ui.splash.SplashScreen
import com.example.SIMATS.ui.splash.SplashViewModel
import com.example.SIMATS.ui.symptom.SymptomLoggerScreen
import com.example.SIMATS.ui.symptom.SymptomLoggerViewModel
import com.example.SIMATS.ui.weight.WeightTrackerScreen
import com.example.SIMATS.ui.weight.WeightTrackerViewModel

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
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
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
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.HealthReport -> {
            val healthReportViewModel: HealthReportViewModel = viewModel()
            HealthReportScreen(
                viewModel = healthReportViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.WeightTracker -> {
            val weightViewModel: WeightTrackerViewModel = viewModel()
            WeightTrackerScreen(
                viewModel = weightViewModel,
                onNavigateBack = {
                    currentScreen = Screen.BloodPressure
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.BloodPressure -> {
            val bpViewModel: BloodPressureViewModel = viewModel()
            BloodPressureScreen(
                viewModel = bpViewModel,
                onNavigateBack = {
                    currentScreen = Screen.WeightTracker
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.BabyDevelopment -> {
            val babyViewModel: BabyDevelopmentViewModel = viewModel()
            BabyDevelopmentScreen(
                viewModel = babyViewModel,
                onNavigateBack = {
                    currentScreen = Screen.BloodPressure
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.AiChat -> {
            val aiViewModel: AiChatViewModel = viewModel()
            AiChatScreen(
                viewModel = aiViewModel,
                onNavigateBack = {
                    currentScreen = Screen.BabyDevelopment
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.Symptom -> {
            val symptomViewModel: SymptomLoggerViewModel = viewModel()
            SymptomLoggerScreen(
                viewModel = symptomViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AiChat
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.BabyDevelopment
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.Nutrition -> {
            val nutritionViewModel: NutritionPlanViewModel = viewModel()
            NutritionPlanScreen(
                viewModel = nutritionViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Symptom
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.BabyDevelopment
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.Medication -> {
            val medicationViewModel: MedicationRemindersViewModel = viewModel()
            MedicationRemindersScreen(
                viewModel = medicationViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Nutrition
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.BabyDevelopment
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.AppointmentsCalendar -> {
            val calendarViewModel: AppointmentsCalendarViewModel = viewModel()
            AppointmentsCalendarScreen(
                viewModel = calendarViewModel,
                onNavigateBack = {
                    currentScreen = Screen.Medication
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.BabyDevelopment
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.MedicalReports -> {
            val reportsViewModel: MedicalReportsViewModel = viewModel()
            MedicalReportsScreen(
                viewModel = reportsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AppointmentsCalendar
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.BabyDevelopment
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.Notifications -> {
            val notificationsViewModel: NotificationsViewModel = viewModel()
            NotificationsScreen(
                viewModel = notificationsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.MedicalReports
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.BabyDevelopment
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.DoctorDashboard
                        4 -> currentScreen = Screen.Profile
                    }
                }
            )
        }
        Screen.DoctorDashboard -> {
            val doctorViewModel: MainViewModel = viewModel()
            MainScreen(
                viewModel = doctorViewModel
            )
        }
    }
}
