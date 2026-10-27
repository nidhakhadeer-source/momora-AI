package com.example.SIMATS.ui.login

data class LoginUiState(
    val greetingName: String = "Maya",
    val email: String = "maya.patel@example.com",
    val password: String = "password12345",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
