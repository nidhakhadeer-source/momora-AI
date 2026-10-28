package com.example.SIMATS.ui.medication

import com.example.SIMATS.data.model.MedicationReminderData

data class MedicationRemindersUiState(
    val data: MedicationReminderData? = null,
    val selectedTab: Int = 0,
    val selectedNavIndex: Int = 3,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
