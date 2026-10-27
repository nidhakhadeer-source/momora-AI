package com.example.SIMATS.ui.calendar

data class CalendarDay(
    val dayNumber: Int,
    val isCurrentMonth: Boolean,
    val isToday: Boolean = false,
    val isSelected: Boolean = false,
    val hasAppointment: Boolean = false
)

data class AppointmentItem(
    val id: String,
    val title: String,
    val timeAndDoctor: String,
    val location: String,
    val tagsText: String
)

data class AppointmentsCalendarUiState(
    val title: String = "Care, on your calendar",
    val subtitle: String = "Everything coming up, in one calm place.",
    val monthYearText: String = "October 2026",
    val selectedDateText: String = "Wednesday, 28 October",
    val appointmentCountText: String = "1 appointment",
    val calendarDays: List<CalendarDay> = listOf(
        CalendarDay(28, false), CalendarDay(29, false), CalendarDay(30, false),
        CalendarDay(1, true), CalendarDay(2, true), CalendarDay(3, true), CalendarDay(4, true),
        CalendarDay(5, true), CalendarDay(6, true), CalendarDay(7, true),
        CalendarDay(8, true), CalendarDay(9, true), CalendarDay(10, true), CalendarDay(11, true),
        CalendarDay(12, true), CalendarDay(13, true), CalendarDay(14, true),
        CalendarDay(15, true), CalendarDay(16, true), CalendarDay(17, true), CalendarDay(18, true),
        CalendarDay(19, true), CalendarDay(20, true), CalendarDay(21, true),
        CalendarDay(22, true), CalendarDay(23, true), CalendarDay(24, true), CalendarDay(25, true),
        CalendarDay(26, true, isToday = true), CalendarDay(27, true),
        CalendarDay(28, true, isSelected = true, hasAppointment = true),
        CalendarDay(29, true), CalendarDay(30, true), CalendarDay(31, true), CalendarDay(1, false)
    ),
    val selectedDayAppointments: List<AppointmentItem> = listOf(
        AppointmentItem(
            id = "1",
            title = "28-week antenatal visit",
            timeAndDoctor = "10:30–11:00 AM · Dr Amira Shah",
            location = "Riverside Maternity · Clinic 2",
            tagsText = "Blood tests · Routine check · Your questions"
        )
    ),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
