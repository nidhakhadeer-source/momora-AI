package com.example.SIMATS.ui.healthreport

sealed interface HealthReportUiEvent {
    data class OnNavTabSelected(val index: Int) : HealthReportUiEvent
    data object OnAddReadingClicked : HealthReportUiEvent
    data object OnWeightTrackerClicked : HealthReportUiEvent
    data object OnBloodPressureTrackerClicked : HealthReportUiEvent
    data object OnSymptomCheckerClicked : HealthReportUiEvent
    data object OnBackClicked : HealthReportUiEvent
    data object OnNotificationClicked : HealthReportUiEvent
}
