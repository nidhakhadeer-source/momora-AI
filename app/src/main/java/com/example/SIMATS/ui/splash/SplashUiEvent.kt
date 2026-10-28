package com.example.SIMATS.ui.splash

sealed interface SplashUiEvent {
    data object OnGetStartedClicked : SplashUiEvent
    data object OnLoginClicked : SplashUiEvent
    data class OnPageChanged(val page: Int) : SplashUiEvent
    data object OnNavigationHandled : SplashUiEvent
}
