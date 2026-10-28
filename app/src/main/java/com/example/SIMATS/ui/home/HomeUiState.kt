package com.example.SIMATS.ui.home

import com.example.SIMATS.data.model.PatientDashboardData

data class HomeUiState(
    val data: PatientDashboardData? = null,
    val selectedNavIndex: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
