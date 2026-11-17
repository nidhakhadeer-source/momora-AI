package com.example.SIMATS.ui.doctorschedule

import com.example.SIMATS.data.model.DoctorScheduleData

data class DoctorScheduleUiState(
    val scheduleData: DoctorScheduleData? = null,
    val selectedDate: String? = null,
    val selectedDateTabIndex: Int = 0,
    val selectedNavIndex: Int = 2,
    val isLoading: Boolean = false,
    val isRescheduling: Boolean = false,
    val errorMessage: String? = null,
    val rescheduleSuccessMessage: String? = null
)
