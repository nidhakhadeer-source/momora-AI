package com.example.SIMATS.data.model

data class AppointmentBookingData(
    val doctorName: String,
    val doctorSpecialty: String,
    val monthTitle: String,
    val availableDates: List<CalendarDateItem>,
    val availableTimeSlots: List<String>,
    val defaultType: String,
    val defaultLocationNotes: String
)

data class CalendarDateItem(
    val dayOfWeek: String,
    val dayOfMonth: Int,
    val isSelected: Boolean
)
