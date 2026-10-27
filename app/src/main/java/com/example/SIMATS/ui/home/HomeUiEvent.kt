package com.example.SIMATS.ui.home

sealed interface HomeUiEvent {
    data class OnTabSelected(val tabIndex: Int) : HomeUiEvent
    data object OnStartCheckInClicked : HomeUiEvent
    data object OnViewTrackersClicked : HomeUiEvent
    data object OnPrenatalVitaminClicked : HomeUiEvent
    data object OnAntenatalVisitClicked : HomeUiEvent
    data object OnOpenDailyPlanClicked : HomeUiEvent
    data object OnSavedClicked : HomeUiEvent
    data object OnNotificationsClicked : HomeUiEvent
    data object OnNavigationHandled : HomeUiEvent
}
