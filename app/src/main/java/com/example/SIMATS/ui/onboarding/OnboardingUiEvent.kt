package com.example.SIMATS.ui.onboarding

sealed interface OnboardingUiEvent {
    data object OnContinueClicked : OnboardingUiEvent
    data object OnLoginClicked : OnboardingUiEvent
    data object OnFavoriteClicked : OnboardingUiEvent
    data object OnHelpClicked : OnboardingUiEvent
    data class OnFeatureClicked(val featureId: Int) : OnboardingUiEvent
}
