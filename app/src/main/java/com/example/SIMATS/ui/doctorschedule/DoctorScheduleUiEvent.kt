package com.example.SIMATS.ui.doctorschedule

sealed interface DoctorScheduleUiEvent {
    data class OnDateTabSelected(val index: Int) : DoctorScheduleUiEvent
    data class OnDateSelected(val date: String) : DoctorScheduleUiEvent
    data class OnNavTabSelected(val index: Int) : DoctorScheduleUiEvent
    data class OnPatientClicked(val patientId: String, val patientName: String) : DoctorScheduleUiEvent
    data class OnRescheduleAppointment(
        val appointmentId: Int,
        val newDate: String,
        val newTime: String
    ) : DoctorScheduleUiEvent
    data object OnManageAvailabilityClicked : DoctorScheduleUiEvent
    data object OnCalendarClicked : DoctorScheduleUiEvent
    data object OnRetryClicked : DoctorScheduleUiEvent
    data object OnBackClicked : DoctorScheduleUiEvent
    data object OnNotificationClicked : DoctorScheduleUiEvent
    data object OnClearRescheduleMessage : DoctorScheduleUiEvent
}
