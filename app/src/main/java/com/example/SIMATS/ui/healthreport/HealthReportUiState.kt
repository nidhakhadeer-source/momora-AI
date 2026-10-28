package com.example.SIMATS.ui.healthreport

import com.example.SIMATS.data.model.HealthMonitoringData

data class HealthReportUiState(
    val healthData: HealthMonitoringData? = null,
    val selectedNavIndex: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
