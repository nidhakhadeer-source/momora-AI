package com.example.SIMATS.ui.main

import com.example.SIMATS.data.model.DoctorDashboardData

data class MainUiState(
    val dashboardData: DoctorDashboardData? = null,
    val selectedNavIndex: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
