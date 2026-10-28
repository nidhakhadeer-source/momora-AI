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
            is LoginUiEvent.OnRoleSelected -> {
                _uiState.update { state ->
                    val (bannerTitle, bannerSubtitle) = when (event.role) {
                        "Doctor" -> "Doctor login" to "Access your maternity care dashboard."
                        "Admin" -> "Admin login" to "Manage system access and clinic configurations."
                        else -> "Patient login" to "Continue your personalized pregnancy journey."
                    }
                    state.copy(
                        selectedRole = event.role,
                        roleBannerTitle = bannerTitle,
                        roleBannerSubtitle = bannerSubtitle
                    )
                }
            }
            is LoginUiEvent.OnEmailInputChanged -> {
                _uiState.update { it.copy(emailInput = event.email) }
            }
            is LoginUiEvent.OnPasswordInputChanged -> {
                _uiState.update { it.copy(passwordInput = event.password) }
            }
            LoginUiEvent.OnTogglePasswordVisibility -> {
                _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            LoginUiEvent.OnForgotPasswordClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ResetPassword.route) }
            }
            LoginUiEvent.OnLoginClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.PrivacyConsent.route) }
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
