package com.example.SIMATS.ui.bloodpressure

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class BloodPressureViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(BloodPressureUiState())
    val uiState: StateFlow<BloodPressureUiState> = _uiState.asStateFlow()

    fun onEvent(event: BloodPressureUiEvent) {
        when (event) {
            is BloodPressureUiEvent.OnSystolicChanged -> {
                _uiState.update { it.copy(inputSystolic = event.systolic) }
            }
            is BloodPressureUiEvent.OnDiastolicChanged -> {
                _uiState.update { it.copy(inputDiastolic = event.diastolic) }
            }
            is BloodPressureUiEvent.OnPulseChanged -> {
                _uiState.update { it.copy(inputPulse = event.pulse) }
            }
            BloodPressureUiEvent.OnSaveReadingClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            is BloodPressureUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            BloodPressureUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            BloodPressureUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
