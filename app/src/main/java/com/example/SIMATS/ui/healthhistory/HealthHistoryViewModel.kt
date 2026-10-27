package com.example.SIMATS.ui.healthhistory

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HealthHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HealthHistoryUiState())
    val uiState: StateFlow<HealthHistoryUiState> = _uiState.asStateFlow()

    fun onEvent(event: HealthHistoryUiEvent) {
        when (event) {
            is HealthHistoryUiEvent.OnDiagnosisToggled -> {
                _uiState.update { state ->
                    val newSet = state.selectedDiagnoses.toMutableSet()
                    if (newSet.contains(event.diagnosis)) {
                        newSet.remove(event.diagnosis)
                    } else {
                        newSet.add(event.diagnosis)
                    }
                    state.copy(selectedDiagnoses = newSet)
                }
            }
            is HealthHistoryUiEvent.OnMedicinesChanged -> {
                _uiState.update { it.copy(currentMedicines = event.medicines) }
            }
            is HealthHistoryUiEvent.OnAllergiesChanged -> {
                _uiState.update { it.copy(allergies = event.allergies) }
            }
            is HealthHistoryUiEvent.OnTeamMonitoringChanged -> {
                _uiState.update { it.copy(teamMonitoring = event.monitoring) }
            }
            HealthHistoryUiEvent.OnSaveAndContinueClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.GoalsPreferences.route) }
            }
            HealthHistoryUiEvent.OnSkipLaterClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.GoalsPreferences.route) }
            }
            HealthHistoryUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            HealthHistoryUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
