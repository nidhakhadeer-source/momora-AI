package com.example.SIMATS.ui.weight

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WeightTrackerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WeightTrackerUiState())
    val uiState: StateFlow<WeightTrackerUiState> = _uiState.asStateFlow()

    fun onEvent(event: WeightTrackerUiEvent) {
        when (event) {
            is WeightTrackerUiEvent.OnRangeSelected -> {
                _uiState.update { it.copy(selectedRange = event.range) }
            }
            is WeightTrackerUiEvent.OnInputWeightChanged -> {
                _uiState.update { it.copy(inputWeightText = event.weight) }
            }
            WeightTrackerUiEvent.OnSaveWeightClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.BloodPressure.route) }
            }
            is WeightTrackerUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            WeightTrackerUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            WeightTrackerUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
