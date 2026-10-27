package com.example.SIMATS.ui.splash

sealed interface SplashUiEvent {
    data object OnBeginJourneyClicked : SplashUiEvent
    data object OnAlreadyHaveAccountClicked : SplashUiEvent
    data object OnMoreOptionsClicked : SplashUiEvent
    data object OnNavigationHandled : SplashUiEvent
}
