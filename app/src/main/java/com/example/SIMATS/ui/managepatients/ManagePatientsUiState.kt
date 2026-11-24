package com.example.SIMATS.ui.managepatients

import com.example.SIMATS.data.model.NotificationItem
import com.example.SIMATS.data.model.PatientAccountItem

data class ManagePatientsUiState(
    val patients: List<PatientAccountItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFilterTab: Int = 0, // 0 = All, 1 = Active, 2 = Inactive
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0,
    val selectedNavIndex: Int = 2, // Patients tab active (index 2)
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val actionFeedbackMessage: String? = null,
    // Patient Details Dialog
    val selectedPatient: PatientAccountItem? = null,
    val showPatientDetailsDialog: Boolean = false,
    // Toggle Confirmation Dialog
    val patientToToggle: PatientAccountItem? = null,
    val showToggleConfirmDialog: Boolean = false,
    val isTogglingAccess: Boolean = false,
    // Notifications Dialog
    val showNotificationsDialog: Boolean = false,
    val notificationsList: List<NotificationItem> = emptyList(),
    val isLoadingNotifications: Boolean = false,
    val unreadNotificationCount: Int = 0
)
