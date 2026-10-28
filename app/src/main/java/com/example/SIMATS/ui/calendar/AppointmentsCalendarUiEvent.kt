package com.example.SIMATS.ui.calendar

sealed interface AppointmentsCalendarUiEvent {
    data class OnVisitTypeToggled(val isInPerson: Boolean) : AppointmentsCalendarUiEvent
    data class OnDateSelected(val dayOfMonth: Int) : AppointmentsCalendarUiEvent
    data class OnTimeSlotSelected(val timeSlot: String) : AppointmentsCalendarUiEvent
    data class OnNavTabSelected(val index: Int) : AppointmentsCalendarUiEvent
    data object OnConfirmAppointmentClicked : AppointmentsCalendarUiEvent
    data object OnNextMonthClicked : AppointmentsCalendarUiEvent
    data object OnBackClicked : AppointmentsCalendarUiEvent
    data object OnNotificationClicked : AppointmentsCalendarUiEvent
}
