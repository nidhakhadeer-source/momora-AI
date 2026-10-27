package com.example.SIMATS.ui.exercise

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ExerciseGuidanceViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ExerciseGuidanceUiState())
    val uiState: StateFlow<ExerciseGuidanceUiState> = _uiState.asStateFlow()

    fun onEvent(event: ExerciseGuidanceUiEvent) {
        when (event) {
            is ExerciseGuidanceUiEvent.OnIntensityOptionSelected -> {
                _uiState.update { it.copy(selectedIntensity = event.option) }
            }
            is ExerciseGuidanceUiEvent.OnStepItemClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.SleepInsights.route) }
            }
            ExerciseGuidanceUiEvent.OnBeginSessionClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.SleepInsights.route) }
            }
            is ExerciseGuidanceUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            ExerciseGuidanceUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            ExerciseGuidanceUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
