package com.example.SIMATS.ui.timeline

sealed interface TimelineUiEvent {
    data class OnTrimesterSelected(val trimester: Int) : TimelineUiEvent
    data class OnWeekItemClicked(val weekNumber: Int) : TimelineUiEvent
    data object OnExploreWeekClicked : TimelineUiEvent
    data class OnTabSelected(val tabIndex: Int) : TimelineUiEvent
    data object OnBackClicked : TimelineUiEvent
    data object OnNavigationHandled : TimelineUiEvent
}
