package com.example.SIMATS.ui.patientlist

import com.example.SIMATS.data.model.AssignedPatientItem

data class PatientListUiState(
    val patients: List<AssignedPatientItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFilterTab: Int = 0, // 0 = All, 1 = To review
    val totalCount: Int = 18,
    val reviewCount: Int = 2,
    val selectedNavIndex: Int = 1, // Patients tab active (index 1)
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
