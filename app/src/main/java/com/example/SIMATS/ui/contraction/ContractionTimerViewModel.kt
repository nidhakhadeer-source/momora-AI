package com.example.SIMATS.ui.contraction

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ContractionTimerViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ContractionTimerUiState())
    val uiState: StateFlow<ContractionTimerUiState> = _uiState.asStateFlow()

    fun onEvent(event: ContractionTimerUiEvent) {
        when (event) {
            ContractionTimerUiEvent.OnStartStopContractionClicked -> {
                _uiState.update { state ->
                    val nextActive = !state.isContractionActive
                    state.copy(
                        isContractionActive = nextActive,
                        timerStatusLabel = if (nextActive) "contraction timing in progress" else "ready when you are",
                        timerDisplayText = if (nextActive) "00:45" else "00:00"
                    )
                }
            }
            ContractionTimerUiEvent.OnContactMaternityTeamClicked -> {
                // Action for calling maternity team
            }
            is ContractionTimerUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            ContractionTimerUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            ContractionTimerUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
