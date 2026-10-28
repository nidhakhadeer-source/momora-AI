package com.example.SIMATS.ui.signup

sealed interface SignUpUiEvent {
    data class OnFullNameChanged(val fullName: String) : SignUpUiEvent
    data class OnEmailChanged(val email: String) : SignUpUiEvent
    data class OnDateOfBirthChanged(val dob: String) : SignUpUiEvent
    data class OnPasswordChanged(val password: String) : SignUpUiEvent
    data class OnConfirmPasswordChanged(val password: String) : SignUpUiEvent
    data object OnTogglePasswordVisibility : SignUpUiEvent
    data object OnToggleConfirmPasswordVisibility : SignUpUiEvent
    data class OnTermsAgreedToggled(val agreed: Boolean) : SignUpUiEvent
    data object OnRegisterClicked : SignUpUiEvent
    data object OnLoginClicked : SignUpUiEvent
    data object OnBackClicked : SignUpUiEvent
    data object OnHelpClicked : SignUpUiEvent
}
