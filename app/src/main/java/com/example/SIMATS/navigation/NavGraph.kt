package com.example.SIMATS.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.data.model.UserRole
import com.example.SIMATS.ui.adddoctor.AddDoctorScreen
import com.example.SIMATS.ui.adddoctor.AddDoctorViewModel
import com.example.SIMATS.ui.admindashboard.AdminDashboardScreen
import com.example.SIMATS.ui.admindashboard.AdminDashboardViewModel
import com.example.SIMATS.ui.adminsettings.AdminSettingsScreen
import com.example.SIMATS.ui.adminsettings.AdminSettingsViewModel
import com.example.SIMATS.ui.ai.AiChatScreen
import com.example.SIMATS.ui.ai.AiChatViewModel
import com.example.SIMATS.ui.babydevelopment.BabyDevelopmentScreen
import com.example.SIMATS.ui.babydevelopment.BabyDevelopmentViewModel
import com.example.SIMATS.ui.bloodpressure.BloodPressureScreen
import com.example.SIMATS.ui.bloodpressure.BloodPressureViewModel
import com.example.SIMATS.ui.calendar.AppointmentsCalendarScreen
import com.example.SIMATS.ui.calendar.AppointmentsCalendarViewModel
import com.example.SIMATS.ui.doctoradvice.DoctorAdviceScreen
import com.example.SIMATS.ui.doctoradvice.DoctorAdviceViewModel
import com.example.SIMATS.ui.doctorschedule.DoctorScheduleScreen
import com.example.SIMATS.ui.doctorschedule.DoctorScheduleViewModel
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
import com.example.SIMATS.ui.managedoctors.ManageDoctorsScreen
import com.example.SIMATS.ui.managedoctors.ManageDoctorsViewModel
import com.example.SIMATS.ui.managepatients.ManagePatientsScreen
import com.example.SIMATS.ui.managepatients.ManagePatientsViewModel
import com.example.SIMATS.ui.medicalreports.MedicalReportsScreen
import com.example.SIMATS.ui.medicalreports.MedicalReportsViewModel
import com.example.SIMATS.ui.medication.MedicationRemindersScreen
import com.example.SIMATS.ui.medication.MedicationRemindersViewModel
import com.example.SIMATS.ui.menu.ScreenMenuScreen
import com.example.SIMATS.ui.notifications.NotificationsScreen
import com.example.SIMATS.ui.notifications.NotificationsViewModel
import com.example.SIMATS.ui.nutrition.NutritionPlanScreen
import com.example.SIMATS.ui.nutrition.NutritionPlanViewModel
import com.example.SIMATS.ui.onboarding.OnboardingScreen
import com.example.SIMATS.ui.onboarding.OnboardingViewModel
import com.example.SIMATS.ui.patienthealthmonitoring.PatientHealthMonitoringScreen
import com.example.SIMATS.ui.patienthealthmonitoring.PatientHealthMonitoringViewModel
import com.example.SIMATS.ui.patienthistory.PatientHistoryScreen
import com.example.SIMATS.ui.patienthistory.PatientHistoryViewModel
import com.example.SIMATS.ui.patientlist.PatientListScreen
import com.example.SIMATS.ui.patientlist.PatientListViewModel
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
        Screen.NavMenu -> {
            ScreenMenuScreen(
                onNavigateToScreen = { screen ->
                    currentScreen = screen
                }
            )
        }
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
                onLoginSuccess = { role ->
                    when (role) {
                        UserRole.PATIENT -> currentScreen = Screen.Home
                        UserRole.DOCTOR -> currentScreen = Screen.DoctorDashboard
                        UserRole.ADMIN -> currentScreen = Screen.AdminDashboard
                    }
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
                viewModel = homeViewModel,
                onPregnancyDetailsClick = { currentScreen = Screen.DueDate },
                onBloodPressureClick = { currentScreen = Screen.BloodPressure },
                onWeightClick = { currentScreen = Screen.WeightTracker },
                onAskAiClick = { currentScreen = Screen.AiChat },
                onBabyGrowthClick = { currentScreen = Screen.BabyDevelopment },
                onNutritionClick = { currentScreen = Screen.Nutrition },
                onNotificationClick = { currentScreen = Screen.Notifications },
                onViewAllDailyCareClick = { currentScreen = Screen.Medication },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
                        4 -> currentScreen = Screen.Profile
                    }
                }
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
                        3 -> currentScreen = Screen.AppointmentsCalendar
                        4 -> currentScreen = Screen.Profile
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
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
                    currentScreen = Screen.Home
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.Symptom
                        2 -> currentScreen = Screen.AiChat
                        3 -> currentScreen = Screen.AppointmentsCalendar
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
        Screen.AssignedPatients -> {
            val patientListViewModel: PatientListViewModel = viewModel()
            PatientListScreen(
                viewModel = patientListViewModel,
                onNavigateBack = {
                    currentScreen = Screen.DoctorDashboard
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.DoctorDashboard
                    }
                }
            )
        }
        Screen.PatientHistory -> {
            val patientHistoryViewModel: PatientHistoryViewModel = viewModel()
            PatientHistoryScreen(
                viewModel = patientHistoryViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AssignedPatients
                },
                onOpenHealthMonitoringClick = {
                    currentScreen = Screen.PatientHealthMonitoring
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.DoctorDashboard
                    }
                }
            )
        }
        Screen.PatientHealthMonitoring -> {
            val healthMonitoringViewModel: PatientHealthMonitoringViewModel = viewModel()
            PatientHealthMonitoringScreen(
                viewModel = healthMonitoringViewModel,
                onNavigateBack = {
                    currentScreen = Screen.PatientHistory
                },
                onWriteAdviceClick = {
                    currentScreen = Screen.DoctorAdvice
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.DoctorDashboard
                    }
                }
            )
        }
        Screen.DoctorAdvice -> {
            val adviceViewModel: DoctorAdviceViewModel = viewModel()
            DoctorAdviceScreen(
                viewModel = adviceViewModel,
                onNavigateBack = {
                    currentScreen = Screen.PatientHealthMonitoring
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.DoctorDashboard
                    } else if (index == 2) {
                        currentScreen = Screen.DoctorSchedule
                    }
                }
            )
        }
        Screen.DoctorSchedule -> {
            val doctorScheduleViewModel: DoctorScheduleViewModel = viewModel()
            DoctorScheduleScreen(
                viewModel = doctorScheduleViewModel,
                onNavigateBack = {
                    currentScreen = Screen.DoctorAdvice
                },
                onViewRecordClick = {
                    currentScreen = Screen.PatientHistory
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.DoctorDashboard
                    } else if (index == 1) {
                        currentScreen = Screen.AssignedPatients
                    } else if (index == 3) {
                        currentScreen = Screen.DoctorAdvice
                    }
                }
            )
        }
        Screen.AdminDashboard -> {
            val adminViewModel: AdminDashboardViewModel = viewModel()
            AdminDashboardScreen(
                viewModel = adminViewModel,
                onManageDoctorsClick = {
                    currentScreen = Screen.ManageDoctors
                },
                onManagePatientsClick = {
                    currentScreen = Screen.ManagePatients
                },
                onAddDoctorClick = {
                    currentScreen = Screen.AddDoctor
                },
                onNavTabSelected = { index ->
                    if (index == 3) {
                        currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
        Screen.AddDoctor -> {
            val addDoctorViewModel: AddDoctorViewModel = viewModel()
            AddDoctorScreen(
                viewModel = addDoctorViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AdminDashboard
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.AdminDashboard
                    } else if (index == 1) {
                        currentScreen = Screen.ManageDoctors
                    } else if (index == 2) {
                        currentScreen = Screen.ManagePatients
                    } else if (index == 3) {
                        currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
        Screen.ManageDoctors -> {
            val manageDoctorsViewModel: ManageDoctorsViewModel = viewModel()
            ManageDoctorsScreen(
                viewModel = manageDoctorsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AddDoctor
                },
                onAddDoctorClick = {
                    currentScreen = Screen.AddDoctor
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.AdminDashboard
                    } else if (index == 2) {
                        currentScreen = Screen.ManagePatients
                    } else if (index == 3) {
                        currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
        Screen.ManagePatients -> {
            val managePatientsViewModel: ManagePatientsViewModel = viewModel()
            ManagePatientsScreen(
                viewModel = managePatientsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.ManageDoctors
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.AdminDashboard
                    } else if (index == 1) {
                        currentScreen = Screen.ManageDoctors
                    } else if (index == 3) {
                        currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
        Screen.AdminSettings -> {
            val adminSettingsViewModel: AdminSettingsViewModel = viewModel()
            AdminSettingsScreen(
                viewModel = adminSettingsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.ManagePatients
                },
                onLogoutSuccess = {
                    currentScreen = Screen.Login
                },
                onNavTabSelected = { index ->
                    if (index == 0) {
                        currentScreen = Screen.AdminDashboard
                    } else if (index == 1) {
                        currentScreen = Screen.ManageDoctors
                    } else if (index == 2) {
                        currentScreen = Screen.ManagePatients
                    }
                }
            )
        }
    }
}
