package com.example.SIMATS.ui.signup

data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isTermsAgreed: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccess: Boolean = false
)
