package com.example.SIMATS.ui.symptom

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SymptomLoggerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SymptomLoggerUiState())
    val uiState: StateFlow<SymptomLoggerUiState> = _uiState.asStateFlow()

    fun onEvent(event: SymptomLoggerUiEvent) {
        when (event) {
            is SymptomLoggerUiEvent.OnSymptomSelected -> {
                _uiState.update { it.copy(selectedSymptom = event.symptom) }
            }
            is SymptomLoggerUiEvent.OnSeveritySelected -> {
                _uiState.update { it.copy(selectedSeverity = event.severity) }
            }
            is SymptomLoggerUiEvent.OnStartTimeChanged -> {
                _uiState.update { it.copy(startTime = event.startTime) }
            }
            is SymptomLoggerUiEvent.OnDurationChanged -> {
                _uiState.update { it.copy(duration = event.duration) }
            }
            is SymptomLoggerUiEvent.OnNotesChanged -> {
                _uiState.update { it.copy(notes = event.notes) }
            }
            SymptomLoggerUiEvent.OnSaveSymptomClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            SymptomLoggerUiEvent.OnViewWarningSignsClicked -> {
                // Action for warning signs
            }
            is SymptomLoggerUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            SymptomLoggerUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            SymptomLoggerUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
