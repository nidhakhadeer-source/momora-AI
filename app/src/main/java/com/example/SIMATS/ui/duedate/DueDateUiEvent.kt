package com.example.SIMATS.ui.duedate

sealed interface DueDateUiEvent {
    data class OnDueDateChanged(val dueDate: String) : DueDateUiEvent
    data class OnLmpChanged(val lmp: String) : DueDateUiEvent
    data class OnTrimesterTabSelected(val trimester: Int) : DueDateUiEvent
    data class OnNavTabSelected(val index: Int) : DueDateUiEvent
    data object OnDoctorCardClicked : DueDateUiEvent
    data object OnNextVisitClicked : DueDateUiEvent
    data object OnDismissAppointmentDetail : DueDateUiEvent
    data object OnCancelAppointmentClicked : DueDateUiEvent
    data object OnSaveClicked : DueDateUiEvent
    data object OnDismissSaveSuccess : DueDateUiEvent
    data object OnBackClicked : DueDateUiEvent
    data object OnNotificationClicked : DueDateUiEvent
    data object OnRetryClicked : DueDateUiEvent
}
