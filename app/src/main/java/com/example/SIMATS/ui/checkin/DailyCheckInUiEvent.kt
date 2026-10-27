package com.example.SIMATS.ui.checkin

sealed interface DailyCheckInUiEvent {
    data class OnMoodSelected(val mood: String) : DailyCheckInUiEvent
    data class OnEnergySelected(val energy: String) : DailyCheckInUiEvent
    data class OnSleepDurationChanged(val duration: String) : DailyCheckInUiEvent
    data class OnNotesOnMindChanged(val notes: String) : DailyCheckInUiEvent
    data object OnSaveCheckInClicked : DailyCheckInUiEvent
    data class OnTabSelected(val tabIndex: Int) : DailyCheckInUiEvent
    data object OnBackClicked : DailyCheckInUiEvent
    data object OnNavigationHandled : DailyCheckInUiEvent
}
