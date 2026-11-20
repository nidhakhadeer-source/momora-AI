package com.example.SIMATS.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.NotificationType
import com.example.SIMATS.data.model.UserAuthState
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
import com.example.SIMATS.ui.doctoravailability.DoctorAvailabilityScreen
import com.example.SIMATS.ui.doctoravailability.DoctorAvailabilityViewModel
import com.example.SIMATS.ui.doctornotifications.DoctorNotificationsScreen
import com.example.SIMATS.ui.doctorschedule.DoctorScheduleScreen
import com.example.SIMATS.ui.doctorprofile.DoctorProfileScreen
import com.example.SIMATS.ui.doctorprofile.DoctorProfileViewModel
import com.example.SIMATS.ui.doctorschedule.DoctorScheduleViewModel
import com.example.SIMATS.ui.duedate.DueDateScreen
import com.example.SIMATS.ui.duedate.DueDateViewModel
import com.example.SIMATS.ui.healthreport.HealthReportScreen
import com.example.SIMATS.ui.healthreport.HealthReportViewModel
import com.example.SIMATS.ui.home.HomeScreen
import com.example.SIMATS.ui.home.HomeUiEvent
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
import com.example.SIMATS.ui.patientadvice.PatientDoctorAdviceScreen
import com.example.SIMATS.ui.patientadvice.PatientDoctorAdviceViewModel
import com.example.SIMATS.ui.patienthealthmonitoring.PatientHealthMonitoringScreen
import com.example.SIMATS.ui.patienthealthmonitoring.PatientHealthMonitoringViewModel
import com.example.SIMATS.ui.patienthistory.PatientHistoryScreen
import com.example.SIMATS.ui.patienthistory.PatientHistoryViewModel
import com.example.SIMATS.ui.patientlist.PatientListScreen
import com.example.SIMATS.ui.patientlist.PatientListViewModel
import com.example.SIMATS.ui.profile.ProfileSetupScreen
import com.example.SIMATS.ui.profile.ProfileSetupViewModel
import com.example.SIMATS.ui.roleselect.RoleSelectScreen
import com.example.SIMATS.ui.signup.SignUpScreen
import com.example.SIMATS.ui.signup.SignUpViewModel
import com.example.SIMATS.ui.splash.SplashScreen
import com.example.SIMATS.ui.splash.SplashViewModel
import com.example.SIMATS.ui.symptom.SymptomLoggerScreen
import com.example.SIMATS.ui.symptom.SymptomLoggerViewModel
import com.example.SIMATS.ui.weight.WeightTrackerScreen
import com.example.SIMATS.ui.weight.WeightTrackerViewModel

private val PatientScreens: Set<Screen> = setOf(
    Screen.Home,
    Screen.Profile,
    Screen.DueDate,
    Screen.HealthReport,
    Screen.WeightTracker,
    Screen.BloodPressure,
    Screen.BabyDevelopment,
    Screen.AiChat,
    Screen.Symptom,
    Screen.Nutrition,
    Screen.Medication,
    Screen.AppointmentsCalendar,
    Screen.MedicalReports,
    Screen.Notifications,
    Screen.PatientDoctorAdvice
)

private val DoctorScreens: Set<Screen> = setOf(
    Screen.DoctorDashboard,
    Screen.AssignedPatients,
    Screen.PatientHistory,
    Screen.PatientHealthMonitoring,
    Screen.DoctorAdvice,
    Screen.DoctorSchedule,
    Screen.DoctorAvailability,
    Screen.DoctorProfile,
    Screen.DoctorNotifications
)

private val AdminScreens: Set<Screen> = setOf(
    Screen.AdminDashboard,
    Screen.AddDoctor,
    Screen.ManageDoctors,
    Screen.ManagePatients,
    Screen.AdminSettings
)

/**
 * Route guard:
 * If user role is DOCTOR, never route to patient screens;
 * if user role is PATIENT, never route to doctor screens.
 */
private fun sanitizeScreenForRole(target: Screen): Screen {
    if (!SessionManager.isLoggedIn()) {
        return target
    }
    return when (SessionManager.getUserRole()) {
        UserRole.DOCTOR -> {
            when {
                target == Screen.Notifications -> Screen.DoctorNotifications
                target in PatientScreens -> Screen.DoctorDashboard
                target in AdminScreens -> Screen.DoctorDashboard
                else -> target
            }
        }
        UserRole.PATIENT -> {
            when {
                target in DoctorScreens -> Screen.Home
                target in AdminScreens -> Screen.Home
                else -> target
            }
        }
        UserRole.ADMIN -> {
            when {
                target in PatientScreens -> Screen.AdminDashboard
                target in DoctorScreens -> Screen.AdminDashboard
                else -> target
            }
        }
    }
}

@Composable
fun AppNavHost() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Splash) }
    var selectedLoginRole by remember { mutableStateOf(UserRole.PATIENT) }
    var selectedPatientId by remember { mutableStateOf<String?>(SelectedPatientHolder.selectedPatientId) }
    var selectedPatientName by remember { mutableStateOf<String?>(SelectedPatientHolder.selectedPatientName) }

    LaunchedEffect(Unit) {
        SessionManager.sessionExpiredEvent.collect {
            SelectedPatientHolder.clear()
            selectedPatientId = null
            selectedPatientName = null
            currentScreen = Screen.RoleSelect
        }
    }

    LaunchedEffect(currentScreen) {
        val safe = sanitizeScreenForRole(currentScreen)
        if (safe != currentScreen) {
            currentScreen = safe
        }
    }

    val activeScreen = sanitizeScreenForRole(currentScreen)

    when (activeScreen) {
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
                onNavigateNext = { authState ->
                    when (authState) {
                        is UserAuthState.Authenticated -> {
                            when (authState.role) {
                                UserRole.PATIENT -> currentScreen = Screen.Home
                                UserRole.DOCTOR -> currentScreen = Screen.DoctorDashboard
                                UserRole.ADMIN -> currentScreen = Screen.AdminDashboard
                            }
                        }
                        else -> {
                            if (SessionManager.isOnboardingCompleted()) {
                                currentScreen = Screen.RoleSelect
                            } else {
                                currentScreen = Screen.Onboarding
                            }
                        }
                    }
                }
            )
        }
        Screen.Onboarding -> {
            val onboardingViewModel: OnboardingViewModel = viewModel()
            OnboardingScreen(
                viewModel = onboardingViewModel,
                onNavigateToLogin = {
                    SessionManager.setOnboardingCompleted(true)
                    currentScreen = Screen.RoleSelect
                },
                onGetStartedComplete = {
                    SessionManager.setOnboardingCompleted(true)
                    currentScreen = Screen.SignUp
                }
            )
        }
        Screen.RoleSelect -> {
            RoleSelectScreen(
                onRoleSelected = { role ->
                    selectedLoginRole = role
                    currentScreen = Screen.Login
                },
                onNavigateBack = {
                    currentScreen = Screen.Onboarding
                }
            )
        }
        Screen.Login -> {
            val loginViewModel: LoginViewModel = viewModel()
            LoginScreen(
                viewModel = loginViewModel,
                role = selectedLoginRole,
                onNavigateBack = {
                    currentScreen = Screen.RoleSelect
                },
                onLoginSuccess = { role ->
                    SelectedPatientHolder.clear()
                    selectedPatientId = null
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
                    currentScreen = Screen.RoleSelect
                },
                onNavigateToLogin = {
                    currentScreen = Screen.RoleSelect
                },
                onRegistrationSuccess = {
                    currentScreen = Screen.Home
                }
            )
        }
        Screen.Home -> {
            val homeViewModel: HomeViewModel = viewModel()
            LaunchedEffect(Unit) {
                homeViewModel.onEvent(HomeUiEvent.OnRetryClicked)
            }
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
                onMedicationClick = { currentScreen = Screen.Medication },
                onAppointmentClick = { currentScreen = Screen.AppointmentsCalendar },
                onDoctorAdviceClick = { currentScreen = Screen.PatientDoctorAdvice },
                onMedicalReportsClick = { currentScreen = Screen.MedicalReports },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.DueDate
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
                onLogoutSuccess = {
                    currentScreen = Screen.RoleSelect
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
                onDoctorCardClick = {
                    currentScreen = Screen.PatientDoctorAdvice
                },
                onNextVisitClick = {
                    currentScreen = Screen.AppointmentsCalendar
                },
                onNotificationClick = {
                    currentScreen = Screen.Notifications
                },
                onBabyDevelopmentClick = {
                    currentScreen = Screen.BabyDevelopment
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.Home
                        1 -> currentScreen = Screen.DueDate
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
            LaunchedEffect(Unit) {
                weightViewModel.loadWeightData()
            }
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
            LaunchedEffect(Unit) {
                bpViewModel.loadData()
            }
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
                onNotificationClick = { item ->
                    when (item.type) {
                        NotificationType.APPOINTMENT -> {
                            currentScreen = Screen.AppointmentsCalendar
                        }
                        NotificationType.REMINDER -> {
                            currentScreen = Screen.Medication
                        }
                        NotificationType.REPORT_REVIEW -> {
                            if (item.title.contains("advice", ignoreCase = true) ||
                                item.subtitle.contains("advice", ignoreCase = true)
                            ) {
                                currentScreen = Screen.PatientDoctorAdvice
                            } else {
                                currentScreen = Screen.MedicalReports
                            }
                        }
                        NotificationType.WEEKLY_DISCOVERY -> {
                            currentScreen = Screen.BabyDevelopment
                        }
                    }
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
        Screen.PatientDoctorAdvice -> {
            if (SessionManager.getUserRole() == UserRole.DOCTOR) {
                currentScreen = Screen.DoctorDashboard
            } else {
                val patientAdviceViewModel: PatientDoctorAdviceViewModel = viewModel()
                PatientDoctorAdviceScreen(
                    viewModel = patientAdviceViewModel,
                    onNavigateBack = {
                        currentScreen = Screen.Home
                    },
                    onNotificationClick = {
                        currentScreen = Screen.Notifications
                    },
                    onNavTabSelected = { index ->
                        when (index) {
                            0 -> currentScreen = Screen.Home
                            1 -> currentScreen = Screen.Symptom
                            2 -> currentScreen = Screen.AiChat
                            3 -> currentScreen = Screen.PatientDoctorAdvice
                            4 -> currentScreen = Screen.Profile
                        }
                    }
                )
            }
        }
        Screen.DoctorNotifications -> {
            val notificationsViewModel: NotificationsViewModel = viewModel()
            DoctorNotificationsScreen(
                viewModel = notificationsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.DoctorDashboard
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.DoctorDashboard
                        1 -> currentScreen = Screen.AssignedPatients
                        2 -> currentScreen = Screen.DoctorSchedule
                        3 -> currentScreen = Screen.DoctorAdvice
                    }
                }
            )
        }
        Screen.DoctorDashboard -> {
            val doctorViewModel: MainViewModel = viewModel()
            MainScreen(
                viewModel = doctorViewModel,
                onLogoutSuccess = {
                    SelectedPatientHolder.clear()
                    selectedPatientId = null
                    selectedPatientName = null
                    currentScreen = Screen.RoleSelect
                },
                onProfileClick = {
                    currentScreen = Screen.DoctorProfile
                },
                onViewAssignedPatientsClick = {
                    currentScreen = Screen.AssignedPatients
                },
                onScheduleClick = {
                    currentScreen = Screen.DoctorSchedule
                },
                onNextAppointmentClick = {
                    currentScreen = Screen.DoctorSchedule
                },
                onNotificationClick = {
                    currentScreen = Screen.DoctorNotifications
                },
                onReviewPatientClick = { patientId, patientName ->
                    selectedPatientId = patientId
                    selectedPatientName = patientName
                    SelectedPatientHolder.selectedPatientId = patientId
                    SelectedPatientHolder.selectedPatientName = patientName
                    currentScreen = Screen.PatientHistory
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.DoctorDashboard
                        1 -> currentScreen = Screen.AssignedPatients
                        2 -> currentScreen = Screen.DoctorSchedule
                        3 -> currentScreen = Screen.DoctorAdvice
                    }
                }
            )
        }
        Screen.AssignedPatients -> {
            val patientListViewModel: PatientListViewModel = viewModel()
            PatientListScreen(
                viewModel = patientListViewModel,
                onNavigateBack = {
                    currentScreen = Screen.DoctorDashboard
                },
                onPatientClick = { patientId, patientName ->
                    selectedPatientId = patientId
                    selectedPatientName = patientName
                    SelectedPatientHolder.selectedPatientId = patientId
                    SelectedPatientHolder.selectedPatientName = patientName
                    currentScreen = Screen.PatientHistory
                },
                onNotificationClick = {
                    currentScreen = Screen.DoctorNotifications
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.DoctorDashboard
                        1 -> currentScreen = Screen.AssignedPatients
                        2 -> currentScreen = Screen.DoctorSchedule
                        3 -> currentScreen = Screen.DoctorAdvice
                    }
                }
            )
        }
        Screen.PatientHistory -> {
            val activePatientId = selectedPatientId ?: SelectedPatientHolder.selectedPatientId
            val activePatientName = selectedPatientName ?: SelectedPatientHolder.selectedPatientName
            val patientHistoryViewModel: PatientHistoryViewModel = viewModel(
                key = "patient_history_${activePatientId ?: "none"}"
            )
            LaunchedEffect(activePatientId, activePatientName) {
                if (!activePatientId.isNullOrBlank()) {
                    patientHistoryViewModel.setPatientId(activePatientId, activePatientName)
                }
            }
            PatientHistoryScreen(
                viewModel = patientHistoryViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AssignedPatients
                },
                onOpenHealthMonitoringClick = {
                    currentScreen = Screen.PatientHealthMonitoring
                },
                onNotificationClick = {
                    currentScreen = Screen.DoctorNotifications
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.DoctorDashboard
                        1 -> currentScreen = Screen.AssignedPatients
                        2 -> currentScreen = Screen.DoctorSchedule
                        3 -> currentScreen = Screen.DoctorAdvice
                    }
                }
            )
        }
        Screen.PatientHealthMonitoring -> {
            val activePatientId = selectedPatientId ?: SelectedPatientHolder.selectedPatientId
            val activePatientName = selectedPatientName ?: SelectedPatientHolder.selectedPatientName
            val healthMonitoringViewModel: PatientHealthMonitoringViewModel = viewModel(
                key = "health_monitoring_${activePatientId ?: "none"}"
            )
            LaunchedEffect(activePatientId, activePatientName) {
                if (!activePatientId.isNullOrBlank()) {
                    healthMonitoringViewModel.setPatientId(activePatientId, activePatientName)
                }
            }
            PatientHealthMonitoringScreen(
                viewModel = healthMonitoringViewModel,
                onNavigateBack = {
                    currentScreen = Screen.PatientHistory
                },
                onWriteAdviceClick = {
                    currentScreen = Screen.DoctorAdvice
                },
                onNotificationClick = {
                    currentScreen = Screen.DoctorNotifications
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.DoctorDashboard
                        1 -> currentScreen = Screen.AssignedPatients
                        2 -> currentScreen = Screen.DoctorSchedule
                        3 -> currentScreen = Screen.DoctorAdvice
                    }
                }
            )
        }
        Screen.DoctorAdvice -> {
            val activePatientId = selectedPatientId ?: SelectedPatientHolder.selectedPatientId
            val activePatientName = selectedPatientName ?: SelectedPatientHolder.selectedPatientName
            val adviceViewModel: DoctorAdviceViewModel = viewModel(
                key = "doctor_advice_${activePatientId ?: "none"}"
            )
            LaunchedEffect(activePatientId, activePatientName) {
                if (!activePatientId.isNullOrBlank()) {
                    adviceViewModel.setPatientId(activePatientId, activePatientName)
                }
            }
            DoctorAdviceScreen(
                viewModel = adviceViewModel,
                onNavigateBack = {
                    currentScreen = Screen.DoctorDashboard
                },
                onNotificationClick = {
                    currentScreen = Screen.DoctorNotifications
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.DoctorDashboard
                        1 -> currentScreen = Screen.AssignedPatients
                        2 -> currentScreen = Screen.DoctorSchedule
                        3 -> currentScreen = Screen.DoctorAdvice
                    }
                }
            )
        }
        Screen.DoctorSchedule -> {
            val doctorScheduleViewModel: DoctorScheduleViewModel = viewModel()
            DoctorScheduleScreen(
                viewModel = doctorScheduleViewModel,
                onNavigateBack = {
                    currentScreen = Screen.DoctorDashboard
                },
                onViewRecordClick = {
                    currentScreen = Screen.PatientHistory
                },
                onPatientClick = { patientId, patientName ->
                    selectedPatientId = patientId
                    selectedPatientName = patientName
                    SelectedPatientHolder.selectedPatientId = patientId
                    SelectedPatientHolder.selectedPatientName = patientName
                    currentScreen = Screen.PatientHistory
                },
                onManageAvailabilityClick = {
                    currentScreen = Screen.DoctorAvailability
                },
                onNotificationClick = {
                    currentScreen = Screen.DoctorNotifications
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.DoctorDashboard
                        1 -> currentScreen = Screen.AssignedPatients
                        2 -> currentScreen = Screen.DoctorSchedule
                        3 -> currentScreen = Screen.DoctorAdvice
                    }
                }
            )
        }
        Screen.DoctorAvailability -> {
            val doctorAvailabilityViewModel: DoctorAvailabilityViewModel = viewModel()
            DoctorAvailabilityScreen(
                viewModel = doctorAvailabilityViewModel,
                onNavigateBack = {
                    currentScreen = Screen.DoctorSchedule
                },
                onNotificationClick = {
                    currentScreen = Screen.DoctorNotifications
                }
            )
        }
        Screen.DoctorProfile -> {
            val doctorProfileViewModel: DoctorProfileViewModel = viewModel()
            DoctorProfileScreen(
                viewModel = doctorProfileViewModel,
                onNavigateBack = {
                    currentScreen = Screen.DoctorDashboard
                },
                onLogoutSuccess = {
                    SelectedPatientHolder.clear()
                    selectedPatientId = null
                    currentScreen = Screen.RoleSelect
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.DoctorDashboard
                        1 -> currentScreen = Screen.AssignedPatients
                        2 -> currentScreen = Screen.DoctorSchedule
                        3 -> currentScreen = Screen.DoctorAdvice
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
                    when (index) {
                        0 -> currentScreen = Screen.AdminDashboard
                        1 -> currentScreen = Screen.ManageDoctors
                        2 -> currentScreen = Screen.ManagePatients
                        3 -> currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
        Screen.AddDoctor -> {
            val addDoctorViewModel: AddDoctorViewModel = viewModel()
            AddDoctorScreen(
                viewModel = addDoctorViewModel,
                onNavigateBack = {
                    currentScreen = Screen.ManageDoctors
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.AdminDashboard
                        1 -> currentScreen = Screen.ManageDoctors
                        2 -> currentScreen = Screen.ManagePatients
                        3 -> currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
        Screen.ManageDoctors -> {
            val manageDoctorsViewModel: ManageDoctorsViewModel = viewModel()
            ManageDoctorsScreen(
                viewModel = manageDoctorsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AdminDashboard
                },
                onAddDoctorClick = {
                    currentScreen = Screen.AddDoctor
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.AdminDashboard
                        1 -> currentScreen = Screen.ManageDoctors
                        2 -> currentScreen = Screen.ManagePatients
                        3 -> currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
        Screen.ManagePatients -> {
            val managePatientsViewModel: ManagePatientsViewModel = viewModel()
            ManagePatientsScreen(
                viewModel = managePatientsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AdminDashboard
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.AdminDashboard
                        1 -> currentScreen = Screen.ManageDoctors
                        2 -> currentScreen = Screen.ManagePatients
                        3 -> currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
        Screen.AdminSettings -> {
            val adminSettingsViewModel: AdminSettingsViewModel = viewModel()
            AdminSettingsScreen(
                viewModel = adminSettingsViewModel,
                onNavigateBack = {
                    currentScreen = Screen.AdminDashboard
                },
                onLogoutSuccess = {
                    currentScreen = Screen.RoleSelect
                },
                onNavTabSelected = { index ->
                    when (index) {
                        0 -> currentScreen = Screen.AdminDashboard
                        1 -> currentScreen = Screen.ManageDoctors
                        2 -> currentScreen = Screen.ManagePatients
                        3 -> currentScreen = Screen.AdminSettings
                    }
                }
            )
        }
    }
}
