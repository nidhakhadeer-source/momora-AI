package com.example.SIMATS.ui.onboarding

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class OnboardingViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.OnGetStartedClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.SignUp.route) }
            }
            OnboardingUiEvent.OnLoginClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Login.route) }
            }
            is OnboardingUiEvent.OnFeatureItemClicked -> {
                // Action for feature item
            }
            OnboardingUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            OnboardingUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
