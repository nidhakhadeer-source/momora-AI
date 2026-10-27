package com.example.SIMATS.ui.kickhistory

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class KickHistoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(KickHistoryUiState())
    val uiState: StateFlow<KickHistoryUiState> = _uiState.asStateFlow()

    fun onEvent(event: KickHistoryUiEvent) {
        when (event) {
            is KickHistoryUiEvent.OnRangeSelected -> {
                _uiState.update { it.copy(selectedRange = event.range) }
            }
            KickHistoryUiEvent.OnStartNewSessionClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ContractionTimer.route) }
            }
            KickHistoryUiEvent.OnViewAllClicked -> {
                // View all sessions action
            }
            is KickHistoryUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            KickHistoryUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            KickHistoryUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
