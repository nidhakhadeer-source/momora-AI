package com.example.SIMATS.ui.doctoravailability

import com.example.SIMATS.data.model.DoctorAvailabilitySlot

data class DoctorAvailabilityUiState(
    val weeklyHours: List<DoctorAvailabilitySlot> = emptyList(),
    val slotMinutes: Int = 30,
    val doctorName: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val saveSuccessMessage: String? = null
)
