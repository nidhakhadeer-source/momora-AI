package com.example.SIMATS.ui.goals

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GoalsPreferencesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsPreferencesUiState())
    val uiState: StateFlow<GoalsPreferencesUiState> = _uiState.asStateFlow()

    fun onEvent(event: GoalsPreferencesUiEvent) {
        when (event) {
            is GoalsPreferencesUiEvent.OnTopicToggled -> {
                _uiState.update { state ->
                    val newSet = state.selectedTopics.toMutableSet()
                    if (newSet.contains(event.topic)) {
                        newSet.remove(event.topic)
                    } else {
                        newSet.add(event.topic)
                    }
                    state.copy(selectedTopics = newSet)
                }
            }
            is GoalsPreferencesUiEvent.OnToggleMorningCheckIn -> {
                _uiState.update { it.copy(isMorningCheckInEnabled = event.enabled) }
            }
            is GoalsPreferencesUiEvent.OnToggleEveningReflection -> {
                _uiState.update { it.copy(isEveningReflectionEnabled = event.enabled) }
            }
            is GoalsPreferencesUiEvent.OnFoodPreferenceChanged -> {
                _uiState.update { it.copy(foodPreference = event.foodPref) }
            }
            is GoalsPreferencesUiEvent.OnMeasurementUnitsChanged -> {
                _uiState.update { it.copy(measurementUnits = event.units) }
            }
            GoalsPreferencesUiEvent.OnMeetMomoraClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            GoalsPreferencesUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            GoalsPreferencesUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
