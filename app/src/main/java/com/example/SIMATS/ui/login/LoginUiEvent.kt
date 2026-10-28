package com.example.SIMATS.ui.login

sealed interface LoginUiEvent {
    data class OnRoleSelected(val role: String) : LoginUiEvent
    data class OnEmailInputChanged(val email: String) : LoginUiEvent
    data class OnPasswordInputChanged(val password: String) : LoginUiEvent
    data object OnTogglePasswordVisibility : LoginUiEvent
    data object OnForgotPasswordClicked : LoginUiEvent
    data object OnLoginClicked : LoginUiEvent
    data object OnCreateAccountClicked : LoginUiEvent
    data object OnBackClicked : LoginUiEvent
    data object OnNavigationHandled : LoginUiEvent
}
