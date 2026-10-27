package com.example.SIMATS.ui.signup

data class SignUpUiState(
    val name: String = "Maya Patel",
    val email: String = "maya.patel@example.com",
    val password: String = "password12345",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
