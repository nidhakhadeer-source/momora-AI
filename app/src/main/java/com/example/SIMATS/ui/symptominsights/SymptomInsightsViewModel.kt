package com.example.SIMATS.ui.symptominsights

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SymptomInsightsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SymptomInsightsUiState())
    val uiState: StateFlow<SymptomInsightsUiState> = _uiState.asStateFlow()

    fun onEvent(event: SymptomInsightsUiEvent) {
        when (event) {
            is SymptomInsightsUiEvent.OnTipItemClicked -> {
                // Inspect tip item
            }
            SymptomInsightsUiEvent.OnAskFollowUpClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            SymptomInsightsUiEvent.OnAddQuestionsClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            is SymptomInsightsUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            SymptomInsightsUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            SymptomInsightsUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
