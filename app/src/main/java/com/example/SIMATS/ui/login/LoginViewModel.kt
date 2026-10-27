package com.example.SIMATS.ui.login

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }
            is LoginUiEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }
            LoginUiEvent.OnTogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            LoginUiEvent.OnForgotPasswordClicked -> {
                // Action for forgot password
            }
            LoginUiEvent.OnLoginClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            LoginUiEvent.OnContinueWithGoogleClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            LoginUiEvent.OnCreateAccountClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.SignUp.route) }
            }
            LoginUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            LoginUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
