package com.example.SIMATS.ui.healthhistory

sealed interface HealthHistoryUiEvent {
    data class OnDiagnosisToggled(val diagnosis: String) : HealthHistoryUiEvent
    data class OnMedicinesChanged(val medicines: String) : HealthHistoryUiEvent
    data class OnAllergiesChanged(val allergies: String) : HealthHistoryUiEvent
    data class OnTeamMonitoringChanged(val monitoring: String) : HealthHistoryUiEvent
    data object OnSaveAndContinueClicked : HealthHistoryUiEvent
    data object OnSkipLaterClicked : HealthHistoryUiEvent
    data object OnBackClicked : HealthHistoryUiEvent
    data object OnNavigationHandled : HealthHistoryUiEvent
}
