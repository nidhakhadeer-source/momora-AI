package com.example.SIMATS.ui.calendar

import com.example.SIMATS.data.model.AppointmentBookingData

data class AppointmentsCalendarUiState(
    val bookingData: AppointmentBookingData? = null,
    val isInPerson: Boolean = true,
    val selectedDayOfMonth: Int? = null,
    val selectedDayOfWeek: String? = null,
    val selectedMonthTitle: String? = null,
    val selectedTimeSlot: String? = null,
    val selectedNavIndex: Int = 3,
    val isLoading: Boolean = false,
    val isConfirmedSuccess: Boolean = false,
    val confirmationMessage: String? = null,
    val errorMessage: String? = null
)
