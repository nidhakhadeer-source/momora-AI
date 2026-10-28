package com.example.SIMATS.ui.login

data class LoginUiState(
    val title: String = "Welcome back",
    val subtitle: String = "A secure space for every part of your care.",
    val selectedRole: String = "Patient",
    val roleOptions: List<String> = listOf("Patient", "Doctor", "Admin"),
    val roleBannerTitle: String = "Patient login",
    val roleBannerSubtitle: String = "Continue your personalized pregnancy journey.",
    val emailInput: String = "maya.patel@example.com",
    val passwordInput: String = "••••••••••••",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
