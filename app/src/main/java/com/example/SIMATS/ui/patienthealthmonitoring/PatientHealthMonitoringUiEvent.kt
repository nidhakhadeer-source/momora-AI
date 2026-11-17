package com.example.SIMATS.ui.patienthealthmonitoring

sealed interface PatientHealthMonitoringUiEvent {
    data class OnNavTabSelected(val index: Int) : PatientHealthMonitoringUiEvent
    data class OnDaysSelected(val days: Int) : PatientHealthMonitoringUiEvent
    data object OnBpCardClicked : PatientHealthMonitoringUiEvent
    data object OnWeightCardClicked : PatientHealthMonitoringUiEvent
    data object OnDismissSheet : PatientHealthMonitoringUiEvent
    data object OnWriteAdviceClicked : PatientHealthMonitoringUiEvent
    data object OnRetryClicked : PatientHealthMonitoringUiEvent
    data object OnBackClicked : PatientHealthMonitoringUiEvent
    data object OnNotificationClicked : PatientHealthMonitoringUiEvent
}
