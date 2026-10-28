package com.example.SIMATS.ui.onboarding

import com.example.SIMATS.data.model.OnboardingFeature

data class OnboardingUiState(
    val features: List<OnboardingFeature> = emptyList(),
    val currentPage: Int = 0,
    val totalPages: Int = 3,
    val isLoading: Boolean = false
)
