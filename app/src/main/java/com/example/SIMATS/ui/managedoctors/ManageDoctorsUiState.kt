package com.example.SIMATS.ui.managedoctors

import com.example.SIMATS.data.model.DoctorAccountItem
import com.example.SIMATS.data.model.NotificationItem

data class ManageDoctorsUiState(
    val doctors: List<DoctorAccountItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFilterTab: Int = 0, // 0 = All · 12, 1 = Active · 10, 2 = Inactive · 2
    val totalCount: Int = 12,
    val activeCount: Int = 10,
    val inactiveCount: Int = 2,
    val selectedNavIndex: Int = 1, // Doctors tab active (index 1)
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showNotificationsDialog: Boolean = false,
    val notificationsList: List<NotificationItem> = emptyList(),
    val isLoadingNotifications: Boolean = false,
    val unreadNotificationCount: Int = 0
)
