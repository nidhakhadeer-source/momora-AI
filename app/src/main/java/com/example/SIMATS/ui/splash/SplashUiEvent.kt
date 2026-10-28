package com.example.SIMATS.ui.splash

sealed interface SplashUiEvent {
    data object OnGetStartedClicked : SplashUiEvent
    data object OnFavoriteClicked : SplashUiEvent
    data object OnHelpClicked : SplashUiEvent
}
