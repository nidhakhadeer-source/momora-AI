package com.example.SIMATS.ui.kickcounter

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class KickCounterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(KickCounterUiState())
    val uiState: StateFlow<KickCounterUiState> = _uiState.asStateFlow()

    fun onEvent(event: KickCounterUiEvent) {
        when (event) {
            KickCounterUiEvent.OnFeltMovementClicked -> {
                _uiState.update { it.copy(kickCount = it.kickCount + 1) }
            }
            KickCounterUiEvent.OnFinishAndSaveSessionClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            is KickCounterUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            KickCounterUiEvent.OnHistoryClicked -> {
                // View kick counter history
            }
            KickCounterUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            KickCounterUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
