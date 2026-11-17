package com.example.SIMATS.ui.patienthealthmonitoring

import com.example.SIMATS.data.model.PatientHealthMonitoringData

data class PatientHealthMonitoringUiState(
    val monitoringData: PatientHealthMonitoringData? = null,
    val patientName: String? = null,
    val selectedDays: Int = 7,
    val isBpSheetOpen: Boolean = false,
    val isWeightSheetOpen: Boolean = false,
    val selectedNavIndex: Int = 1, // Patients tab active
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
