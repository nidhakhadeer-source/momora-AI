package com.example.SIMATS.ui.signup

sealed interface SignUpUiEvent {
    data class OnNameChanged(val name: String) : SignUpUiEvent
    data class OnEmailChanged(val email: String) : SignUpUiEvent
    data class OnPasswordChanged(val password: String) : SignUpUiEvent
    data object OnTogglePasswordVisibility : SignUpUiEvent
    data object OnCreateAccountClicked : SignUpUiEvent
    data object OnContinueWithGoogleClicked : SignUpUiEvent
    data object OnLoginClicked : SignUpUiEvent
    data object OnBackClicked : SignUpUiEvent
    data object OnNavigationHandled : SignUpUiEvent
}
