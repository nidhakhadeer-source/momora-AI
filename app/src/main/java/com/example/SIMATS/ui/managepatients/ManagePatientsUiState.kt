package com.example.SIMATS.ui.managepatients

import com.example.SIMATS.data.model.NotificationItem
import com.example.SIMATS.data.model.PatientAccountItem

data class ManagePatientsUiState(
    val patients: List<PatientAccountItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFilterTab: Int = 0, // 0 = All · 128, 1 = Active · 120, 2 = Inactive · 8
    val totalCount: Int = 128,
    val activeCount: Int = 120,
    val inactiveCount: Int = 8,
    val selectedNavIndex: Int = 2, // Patients tab active (index 2)
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showNotificationsDialog: Boolean = false,
    val notificationsList: List<NotificationItem> = emptyList(),
    val isLoadingNotifications: Boolean = false,
    val unreadNotificationCount: Int = 0
)
