package com.example.SIMATS.ui.kickhistory

sealed interface KickHistoryUiEvent {
    data class OnRangeSelected(val range: String) : KickHistoryUiEvent
    data object OnStartNewSessionClicked : KickHistoryUiEvent
    data object OnViewAllClicked : KickHistoryUiEvent
    data class OnTabSelected(val tabIndex: Int) : KickHistoryUiEvent
    data object OnBackClicked : KickHistoryUiEvent
    data object OnNavigationHandled : KickHistoryUiEvent
}
