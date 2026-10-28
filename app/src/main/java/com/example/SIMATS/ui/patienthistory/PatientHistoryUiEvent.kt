package com.example.SIMATS.ui.patienthistory

sealed interface PatientHistoryUiEvent {
    data class OnTabSelected(val index: Int) : PatientHistoryUiEvent
    data class OnMedicationClicked(val medicationId: String) : PatientHistoryUiEvent
    data class OnNavTabSelected(val index: Int) : PatientHistoryUiEvent
    data object OnViewAllReportsClicked : PatientHistoryUiEvent
    data object OnOpenHealthMonitoringClicked : PatientHistoryUiEvent
    data object OnBackClicked : PatientHistoryUiEvent
    data object OnNotificationClicked : PatientHistoryUiEvent
}
