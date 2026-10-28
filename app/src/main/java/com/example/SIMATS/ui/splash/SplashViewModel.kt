package com.example.SIMATS.ui.splash

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SplashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun onEvent(event: SplashUiEvent) {
        when (event) {
            SplashUiEvent.OnGetStartedClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.SignUp.route) }
            }
            SplashUiEvent.OnLoginClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Login.route) }
            }
            is SplashUiEvent.OnPageChanged -> {
                _uiState.update { it.copy(currentPage = event.page) }
            }
            SplashUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null) }
            }
        }
    }
}
