package com.example.SIMATS.ui.calendar

import com.example.SIMATS.data.model.AppointmentBookingData

data class AppointmentsCalendarUiState(
    val bookingData: AppointmentBookingData? = null,
    val isInPerson: Boolean = true,
    val selectedDayOfMonth: Int = 29,
    val selectedTimeSlot: String = "10:30",
    val selectedNavIndex: Int = 3,
    val isLoading: Boolean = false,
    val isConfirmedSuccess: Boolean = false,
    val errorMessage: String? = null
)
