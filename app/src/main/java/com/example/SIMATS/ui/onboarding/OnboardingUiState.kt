package com.example.SIMATS.ui.onboarding

import com.example.SIMATS.data.model.OnboardingFeature

data class OnboardingUiState(
    val currentPage: Int = 0, // 0 = Welcome, 1 = Insights & Guidance
    val totalPages: Int = 3,
    val features: List<OnboardingFeature> = emptyList(),
    val isLoading: Boolean = false
)
