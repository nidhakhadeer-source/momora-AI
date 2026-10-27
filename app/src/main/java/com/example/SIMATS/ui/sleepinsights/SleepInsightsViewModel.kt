package com.example.SIMATS.ui.sleepinsights

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SleepInsightsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SleepInsightsUiState())
    val uiState: StateFlow<SleepInsightsUiState> = _uiState.asStateFlow()

    fun onEvent(event: SleepInsightsUiEvent) {
        when (event) {
            SleepInsightsUiEvent.OnGentleIdeaClicked -> {
                // Action for gentle idea
            }
            SleepInsightsUiEvent.OnAddSleepMoodEntryClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.DailyCheckIn.route) }
            }
            is SleepInsightsUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            SleepInsightsUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            SleepInsightsUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
