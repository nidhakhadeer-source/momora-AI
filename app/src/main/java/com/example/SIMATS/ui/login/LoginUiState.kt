package com.example.SIMATS.ui.login

import com.example.SIMATS.data.model.UserRole

data class LoginUiState(
    val selectedRole: UserRole = UserRole.PATIENT,
    val email: String = "maya.patel@example.com",
    val password: String = "••••••••••••",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)
