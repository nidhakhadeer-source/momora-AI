package com.example.SIMATS.ui.duedate

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DueDateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DueDateUiState())
    val uiState: StateFlow<DueDateUiState> = _uiState.asStateFlow()

    fun onEvent(event: DueDateUiEvent) {
        when (event) {
            is DueDateUiEvent.OnCalculationMethodSelected -> {
                _uiState.update { it.copy(selectedCalculationMethod = event.method) }
            }
            is DueDateUiEvent.OnLastPeriodDateChanged -> {
                _uiState.update { it.copy(lastPeriodDate = event.date) }
            }
            is DueDateUiEvent.OnCycleLengthChanged -> {
                _uiState.update { it.copy(cycleLength = event.cycleLength) }
            }
            DueDateUiEvent.OnUseDueDateClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.HealthHistory.route) }
            }
            DueDateUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            DueDateUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
