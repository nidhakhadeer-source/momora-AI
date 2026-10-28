package com.example.SIMATS.ui.patienthistory

import com.example.SIMATS.data.model.PatientMedicalHistoryData

data class PatientHistoryUiState(
    val historyData: PatientMedicalHistoryData? = null,
    val selectedTab: Int = 0, // 0 = Summary, 1 = Reports · 3, 2 = Visits
    val reportsCount: Int = 3,
    val selectedNavIndex: Int = 1, // Patients tab active (index 1)
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
