package com.example.SIMATS.ui.admindashboard

import com.example.SIMATS.data.model.AdminDashboardData

data class AdminDashboardUiState(
    val dashboardData: AdminDashboardData? = null,
    val selectedNavIndex: Int = 0, // Overview tab active
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
