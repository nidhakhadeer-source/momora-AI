package com.example.SIMATS.ui.doctoradvice

import com.example.SIMATS.data.model.DoctorAdviceData

data class DoctorAdviceUiState(
    val adviceData: DoctorAdviceData? = null,
    val patientName: String? = null,
    val subjectText: String = "",
    val adviceText: String = "",
    val isSaveCopyChecked: Boolean = true,
    val selectedNavIndex: Int = 3, // Advice tab active (index 3)
    val isSymptomTimelineSheetOpen: Boolean = false,
    val isLoading: Boolean = false,
    val isSending: Boolean = false,
    val isSentSuccess: Boolean = false,
    val errorMessage: String? = null
)
