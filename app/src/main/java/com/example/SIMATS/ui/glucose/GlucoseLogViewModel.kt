package com.example.SIMATS.ui.glucose

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GlucoseLogViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(GlucoseLogUiState())
    val uiState: StateFlow<GlucoseLogUiState> = _uiState.asStateFlow()

    fun onEvent(event: GlucoseLogUiEvent) {
        when (event) {
            is GlucoseLogUiEvent.OnBloodGlucoseInputChanged -> {
                _uiState.update { it.copy(bloodGlucoseInput = event.value) }
            }
            is GlucoseLogUiEvent.OnTimingSelected -> {
                _uiState.update { it.copy(selectedTiming = event.timing) }
            }
            is GlucoseLogUiEvent.OnDateTimeChanged -> {
                _uiState.update { it.copy(dateTimeInput = event.dateTime) }
            }
            GlucoseLogUiEvent.OnSaveReadingClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.HydrationLog.route) }
            }
            is GlucoseLogUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            GlucoseLogUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            GlucoseLogUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
