package com.example.SIMATS.ui.doctorschedule

import com.example.SIMATS.data.model.DoctorScheduleData

data class DoctorScheduleUiState(
    val scheduleData: DoctorScheduleData? = null,
    val selectedDateTabIndex: Int = 2, // 29 Oct active (index 2)
    val selectedNavIndex: Int = 2, // Schedule tab active (index 2)
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
