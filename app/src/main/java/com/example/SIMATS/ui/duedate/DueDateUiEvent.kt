package com.example.SIMATS.ui.duedate

sealed interface DueDateUiEvent {
    data class OnDueDateChanged(val dueDate: String) : DueDateUiEvent
    data class OnNavTabSelected(val index: Int) : DueDateUiEvent
    data object OnSaveClicked : DueDateUiEvent
    data object OnBackClicked : DueDateUiEvent
    data object OnNotificationClicked : DueDateUiEvent
    data object OnDoctorCardClicked : DueDateUiEvent
    data object OnNextVisitClicked : DueDateUiEvent
}
