package com.example.SIMATS.ui.sleepinsights

sealed interface SleepInsightsUiEvent {
    data object OnGentleIdeaClicked : SleepInsightsUiEvent
    data object OnAddSleepMoodEntryClicked : SleepInsightsUiEvent
    data class OnTabSelected(val tabIndex: Int) : SleepInsightsUiEvent
    data object OnBackClicked : SleepInsightsUiEvent
    data object OnNavigationHandled : SleepInsightsUiEvent
}
