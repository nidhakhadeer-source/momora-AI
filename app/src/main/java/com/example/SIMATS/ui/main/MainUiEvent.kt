package com.example.SIMATS.ui.main

sealed interface MainUiEvent {
    data class OnNavTabSelected(val index: Int) : MainUiEvent
    data class OnReviewPatientClicked(val patientId: String) : MainUiEvent
    data object OnViewAssignedPatientsClicked : MainUiEvent
    data object OnScheduleClicked : MainUiEvent
    data object OnNextAppointmentClicked : MainUiEvent
    data object OnNotificationClicked : MainUiEvent
}
