package com.example.SIMATS.ui.checkin

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DailyCheckInViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DailyCheckInUiState())
    val uiState: StateFlow<DailyCheckInUiState> = _uiState.asStateFlow()

    fun onEvent(event: DailyCheckInUiEvent) {
        when (event) {
            is DailyCheckInUiEvent.OnMoodSelected -> {
                _uiState.update { it.copy(selectedMood = event.mood) }
            }
            is DailyCheckInUiEvent.OnEnergySelected -> {
                _uiState.update { it.copy(selectedEnergy = event.energy) }
            }
            is DailyCheckInUiEvent.OnSleepDurationChanged -> {
                _uiState.update { it.copy(sleepDuration = event.duration) }
            }
            is DailyCheckInUiEvent.OnNotesOnMindChanged -> {
                _uiState.update { it.copy(notesOnMind = event.notes) }
            }
            DailyCheckInUiEvent.OnSaveCheckInClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.SymptomLogger.route) }
            }
            is DailyCheckInUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            DailyCheckInUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            DailyCheckInUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
