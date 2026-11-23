package com.example.SIMATS.ui.admindashboard

import com.example.SIMATS.data.model.AdminDashboardData
import com.example.SIMATS.data.model.NotificationItem

data class AdminDashboardUiState(
    val dashboardData: AdminDashboardData? = null,
    val selectedNavIndex: Int = 0, // Overview tab active
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val adminName: String = "",
    val adminPhotoUrl: String? = null,
    val unreadNotificationCount: Int = 0,
    val showNotificationsDialog: Boolean = false,
    val notificationsList: List<NotificationItem> = emptyList(),
    val isLoadingNotifications: Boolean = false
)
