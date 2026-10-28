package com.example.SIMATS.ui.onboarding

import com.example.SIMATS.R

data class OnboardingFeatureItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int
)

data class OnboardingUiState(
    val headline: String = "Small insights.\nMeaningful reassurance.",
    val subtitle: String = "Follow your pregnancy with thoughtful tools and a connected care team.",
    val features: List<OnboardingFeatureItem> = listOf(
        OnboardingFeatureItem("Understand your wellbeing", "Track vitals, symptoms and weekly growth.", R.drawable.ic_pulse_wave),
        OnboardingFeatureItem("Ask, learn and prepare", "AI guidance alongside your doctor's care.", R.drawable.ic_sparkles)
    ),
    val disclaimerText: String = "AI guidance is educational, not a diagnosis. It does not replace your maternity team.",
    val currentPage: Int = 1,
    val pageCount: Int = 2,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
