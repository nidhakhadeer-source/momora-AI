package com.example.SIMATS.ui.managedoctors

import com.example.SIMATS.data.model.DoctorAccountItem
import com.example.SIMATS.data.model.NotificationItem

data class ManageDoctorsUiState(
    val doctors: List<DoctorAccountItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFilterTab: Int = 0, // 0 = All, 1 = Active, 2 = Inactive
    val totalCount: Int = 0,
    val activeCount: Int = 0,
    val inactiveCount: Int = 0,
    val selectedNavIndex: Int = 1, // Doctors tab active (index 1)
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val actionFeedbackMessage: String? = null,

    // Doctor Details Dialog
    val selectedDoctor: DoctorAccountItem? = null,
    val showDoctorDetailsDialog: Boolean = false,

    // Toggle Confirmation Dialog
    val doctorToToggle: DoctorAccountItem? = null,
    val showToggleConfirmDialog: Boolean = false,
    val isTogglingStatus: Boolean = false,

    // Edit Doctor Dialog
    val doctorToEdit: DoctorAccountItem? = null,
    val showEditDoctorDialog: Boolean = false,
    val editNameInput: String = "",
    val editEmailInput: String = "",
    val editSpecialtyInput: String = "",
    val editLicenseIdInput: String = "",
    val editClinicInput: String = "",
    val isUpdatingDoctor: Boolean = false,
    val editErrorMessage: String? = null,

    // Notifications Dialog
    val showNotificationsDialog: Boolean = false,
    val notificationsList: List<NotificationItem> = emptyList(),
    val isLoadingNotifications: Boolean = false,
    val unreadNotificationCount: Int = 0
)
