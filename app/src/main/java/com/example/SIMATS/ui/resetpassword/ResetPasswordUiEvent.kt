package com.example.SIMATS.ui.resetpassword

sealed interface ResetPasswordUiEvent {
    data class OnEmailChanged(val email: String) : ResetPasswordUiEvent
    data object OnResendResetLinkClicked : ResetPasswordUiEvent
    data object OnBackToLoginClicked : ResetPasswordUiEvent
    data object OnContactSupportClicked : ResetPasswordUiEvent
    data object OnBackClicked : ResetPasswordUiEvent
    data object OnNavigationHandled : ResetPasswordUiEvent
}
