package com.example.SIMATS.ui.duedate

import com.example.SIMATS.data.model.PregnancyDetailsData

data class DueDateUiState(
    val pregnancyData: PregnancyDetailsData? = null,
    val estimatedDueDate: String = initialMetrics.eddFormatted,
    val lastMenstrualPeriod: String = initialMetrics.lmpFormatted,
    val gestationalAgeText: String = initialMetrics.gestationalAgeText,
    val gestationalWeeks: Int = initialMetrics.weeks,
    val gestationalDays: Int = initialMetrics.days,
    val currentTrimester: Int = initialMetrics.trimester,
    val currentTrimesterText: String = initialMetrics.trimesterText,
    val countdownText: String = initialMetrics.countdownText,
    val remainingDaysCount: Int = initialMetrics.remainingDays,
    val progressFraction: Float = initialMetrics.progress,
    val selectedTrimesterView: Int = initialMetrics.trimester,
    val isAppointmentDetailOpen: Boolean = false,
    val appointmentStatus: String = "Confirmed",
    val selectedNavIndex: Int = 1,
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
) {
    companion object {
        private val initialMetrics = PregnancyCalculator.calculateMetrics("09 May 2026", "13 Feb 2027")
    }
}
