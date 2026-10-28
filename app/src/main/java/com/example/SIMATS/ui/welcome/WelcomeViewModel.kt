package com.example.SIMATS.ui.welcome

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class WelcomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeUiState())
    val uiState: StateFlow<WelcomeUiState> = _uiState.asStateFlow()

    fun onEvent(event: WelcomeUiEvent) {
        when (event) {
            WelcomeUiEvent.OnContinueClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Onboarding.route) }
            }
            WelcomeUiEvent.OnLoginClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Login.route) }
            }
            is WelcomeUiEvent.OnFeatureItemClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Onboarding.route) }
            }
            WelcomeUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null) }
            }
        }
    }
}
