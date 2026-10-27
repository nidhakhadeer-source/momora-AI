package com.example.SIMATS.ui.calendar

sealed interface AppointmentsCalendarUiEvent {
    data class OnDaySelected(val day: CalendarDay) : AppointmentsCalendarUiEvent
    data object OnPreviousMonthClicked : AppointmentsCalendarUiEvent
    data object OnNextMonthClicked : AppointmentsCalendarUiEvent
    data object OnAddAppointmentClicked : AppointmentsCalendarUiEvent
    data class OnAppointmentDetailsClicked(val id: String) : AppointmentsCalendarUiEvent
    data class OnTabSelected(val tabIndex: Int) : AppointmentsCalendarUiEvent
    data object OnBackClicked : AppointmentsCalendarUiEvent
    data object OnNavigationHandled : AppointmentsCalendarUiEvent
}
