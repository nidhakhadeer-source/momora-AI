package com.example.SIMATS.ui.kickcounter

sealed interface KickCounterUiEvent {
    data object OnFeltMovementClicked : KickCounterUiEvent
    data object OnFinishAndSaveSessionClicked : KickCounterUiEvent
    data class OnTabSelected(val tabIndex: Int) : KickCounterUiEvent
    data object OnHistoryClicked : KickCounterUiEvent
    data object OnBackClicked : KickCounterUiEvent
    data object OnNavigationHandled : KickCounterUiEvent
}
