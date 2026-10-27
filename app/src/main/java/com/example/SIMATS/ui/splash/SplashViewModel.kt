package com.example.SIMATS.ui.splash

import androidx.lifecycle.ViewModel
import com.example.SIMATS.data.repository.SplashRepository
import com.example.SIMATS.data.repository.SplashRepositoryImpl
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SplashViewModel(
    private val repository: SplashRepository = SplashRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun onEvent(event: SplashUiEvent) {
        when (event) {
            SplashUiEvent.OnBeginJourneyClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.SignUp.route) }
            }
            SplashUiEvent.OnAlreadyHaveAccountClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Login.route) }
            }
            SplashUiEvent.OnMoreOptionsClicked -> {
                // Action for options menu if needed
            }
            SplashUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null) }
            }
        }
    }
}
