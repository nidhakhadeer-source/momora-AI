package com.example.SIMATS.ui.signup

data class SignUpUiState(
    val fullName: String = "Maya Patel",
    val email: String = "maya.patel@example.com",
    val dateOfBirth: String = "14 May 1997",
    val password: String = "••••••••••••",
    val confirmPassword: String = "••••••••••••",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isTermsAgreed: Boolean = true,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccess: Boolean = false
)
