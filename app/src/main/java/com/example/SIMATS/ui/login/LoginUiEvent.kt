package com.example.SIMATS.ui.login

sealed interface LoginUiEvent {
    data class OnEmailChanged(val email: String) : LoginUiEvent
    data class OnPasswordChanged(val password: String) : LoginUiEvent
    data object OnTogglePasswordVisibility : LoginUiEvent
    data object OnForgotPasswordClicked : LoginUiEvent
    data object OnLoginClicked : LoginUiEvent
    data object OnContinueWithGoogleClicked : LoginUiEvent
    data object OnCreateAccountClicked : LoginUiEvent
    data object OnBackClicked : LoginUiEvent
    data object OnNavigationHandled : LoginUiEvent
}
