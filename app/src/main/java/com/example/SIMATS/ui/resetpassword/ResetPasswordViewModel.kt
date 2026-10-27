package com.example.SIMATS.ui.resetpassword

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ResetPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onEvent(event: ResetPasswordUiEvent) {
        when (event) {
            is ResetPasswordUiEvent.OnEmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }
            ResetPasswordUiEvent.OnResendResetLinkClicked -> {
                // Action for resending link
            }
            ResetPasswordUiEvent.OnBackToLoginClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Login.route) }
            }
            ResetPasswordUiEvent.OnContactSupportClicked -> {
                // Action for contact support
            }
            ResetPasswordUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            ResetPasswordUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
