package com.example.SIMATS.ui.patientadvice

import com.example.SIMATS.data.model.PatientAdviceItem

data class PatientDoctorAdviceUiState(
    val adviceList: List<PatientAdviceItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
