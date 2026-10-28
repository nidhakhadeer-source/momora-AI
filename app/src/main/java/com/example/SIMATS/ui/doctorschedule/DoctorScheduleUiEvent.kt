package com.example.SIMATS.ui.doctorschedule

sealed interface DoctorScheduleUiEvent {
    data class OnDateTabSelected(val index: Int) : DoctorScheduleUiEvent
    data class OnNavTabSelected(val index: Int) : DoctorScheduleUiEvent
    data object OnRescheduleClicked : DoctorScheduleUiEvent
    data object OnViewRecordClicked : DoctorScheduleUiEvent
    data object OnManageAvailabilityClicked : DoctorScheduleUiEvent
    data object OnCalendarClicked : DoctorScheduleUiEvent
    data object OnBackClicked : DoctorScheduleUiEvent
    data object OnNotificationClicked : DoctorScheduleUiEvent
}
