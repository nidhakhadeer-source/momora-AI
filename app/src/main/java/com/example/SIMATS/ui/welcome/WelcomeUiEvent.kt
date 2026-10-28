package com.example.SIMATS.ui.welcome

sealed interface WelcomeUiEvent {
    data object OnContinueClicked : WelcomeUiEvent
    data object OnLoginClicked : WelcomeUiEvent
    data class OnFeatureItemClicked(val title: String) : WelcomeUiEvent
    data object OnNavigationHandled : WelcomeUiEvent
}
