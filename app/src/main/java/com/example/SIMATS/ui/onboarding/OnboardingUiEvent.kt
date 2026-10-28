package com.example.SIMATS.ui.onboarding

sealed interface OnboardingUiEvent {
    data object OnGetStartedClicked : OnboardingUiEvent
    data object OnLoginClicked : OnboardingUiEvent
    data class OnFeatureItemClicked(val title: String) : OnboardingUiEvent
    data object OnBackClicked : OnboardingUiEvent
    data object OnNavigationHandled : OnboardingUiEvent
}
