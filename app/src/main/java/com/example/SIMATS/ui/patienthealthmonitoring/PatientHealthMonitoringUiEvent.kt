package com.example.SIMATS.ui.patienthealthmonitoring

sealed interface PatientHealthMonitoringUiEvent {
    data class OnNavTabSelected(val index: Int) : PatientHealthMonitoringUiEvent
    data object OnWriteAdviceClicked : PatientHealthMonitoringUiEvent
    data object OnBackClicked : PatientHealthMonitoringUiEvent
    data object OnNotificationClicked : PatientHealthMonitoringUiEvent
}
