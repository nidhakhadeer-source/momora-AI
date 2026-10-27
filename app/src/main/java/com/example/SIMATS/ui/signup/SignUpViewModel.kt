package com.example.SIMATS.ui.signup

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.OnNameChanged -> {
                _uiState.update { it.copy(name = event.name) }
            }
            is SignUpUiEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }
            is SignUpUiEvent.OnPasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }
            SignUpUiEvent.OnTogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            SignUpUiEvent.OnCreateAccountClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.PrivacyConsent.route) }
            }
            SignUpUiEvent.OnContinueWithGoogleClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.PrivacyConsent.route) }
            }
            SignUpUiEvent.OnLoginClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Login.route) }
            }
            SignUpUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            SignUpUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
