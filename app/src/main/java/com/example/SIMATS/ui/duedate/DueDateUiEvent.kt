package com.example.SIMATS.ui.duedate

sealed interface DueDateUiEvent {
    data class OnCalculationMethodSelected(val method: String) : DueDateUiEvent
    data class OnLastPeriodDateChanged(val date: String) : DueDateUiEvent
    data class OnCycleLengthChanged(val cycleLength: String) : DueDateUiEvent
    data object OnUseDueDateClicked : DueDateUiEvent
    data object OnBackClicked : DueDateUiEvent
    data object OnNavigationHandled : DueDateUiEvent
}
