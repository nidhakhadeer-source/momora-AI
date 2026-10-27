package com.example.SIMATS.ui.resetpassword

data class ResetPasswordUiState(
    val email: String = "maya.patel@example.com",
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
