package com.example.SIMATS.ui.duedate

import com.example.SIMATS.data.model.PregnancyDetailsData

data class DueDateUiState(
    val pregnancyData: PregnancyDetailsData? = null,
    val estimatedDueDate: String = "13 Feb 2027",
    val lastMenstrualPeriod: String = "09 May 2026",
    val gestationalAgeText: String = "24 weeks, 3 days",
    val gestationalWeeks: Int = 24,
    val gestationalDays: Int = 3,
    val currentTrimester: Int = 2,
    val currentTrimesterText: String = "SECOND TRIMESTER",
    val countdownText: String = "15 weeks, 4 days until your estimated due date.",
    val remainingDaysCount: Int = 109,
    val progressFraction: Float = 0.61f,
    val selectedTrimesterView: Int = 2,
    val isAppointmentDetailOpen: Boolean = false,
    val appointmentStatus: String = "Confirmed",
    val selectedNavIndex: Int = 1,
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)
