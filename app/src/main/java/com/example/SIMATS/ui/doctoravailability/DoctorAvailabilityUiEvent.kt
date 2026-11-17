package com.example.SIMATS.ui.doctoravailability

sealed interface DoctorAvailabilityUiEvent {
    data class OnDayToggled(val weekday: String, val isActive: Boolean) : DoctorAvailabilityUiEvent
    data class OnTimeChanged(val weekday: String, val startTime: String, val endTime: String) : DoctorAvailabilityUiEvent
    data class OnSlotMinutesChanged(val minutes: Int) : DoctorAvailabilityUiEvent
    data object OnSaveClicked : DoctorAvailabilityUiEvent
    data object OnRetryClicked : DoctorAvailabilityUiEvent
    data object OnBackClicked : DoctorAvailabilityUiEvent
    data object OnNotificationClicked : DoctorAvailabilityUiEvent
    data object OnClearMessages : DoctorAvailabilityUiEvent
}
