package com.example.SIMATS.ui.contraction

sealed interface ContractionTimerUiEvent {
    data object OnStartStopContractionClicked : ContractionTimerUiEvent
    data object OnContactMaternityTeamClicked : ContractionTimerUiEvent
    data class OnTabSelected(val tabIndex: Int) : ContractionTimerUiEvent
    data object OnBackClicked : ContractionTimerUiEvent
    data object OnNavigationHandled : ContractionTimerUiEvent
}
