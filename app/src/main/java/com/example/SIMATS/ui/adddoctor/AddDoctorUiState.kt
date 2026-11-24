package com.example.SIMATS.ui.adddoctor

import com.example.SIMATS.data.model.AddDoctorData
import com.example.SIMATS.data.model.NotificationItem

data class AddDoctorUiState(
    val formData: AddDoctorData? = null,
    val nameInput: String = "",
    val emailInput: String = "",
    val specialtyInput: String = "",
    val licenseIdInput: String = "",
    val clinicInput: String = "",
    val isSendActivationChecked: Boolean = true,
    val selectedNavIndex: Int = 1, // Doctors tab active (index 1)
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,
    val isCreatedSuccess: Boolean = false,
    val successMessage: String? = null,
    val errorMessage: String? = null,
    val isClinicDropdownExpanded: Boolean = false,
    val showNotificationsDialog: Boolean = false,
    val notificationsList: List<NotificationItem> = emptyList(),
    val isLoadingNotifications: Boolean = false,
    val unreadNotificationCount: Int = 0
)
