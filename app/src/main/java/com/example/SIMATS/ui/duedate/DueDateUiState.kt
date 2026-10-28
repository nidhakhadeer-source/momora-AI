package com.example.SIMATS.ui.duedate

import com.example.SIMATS.data.model.PregnancyDetailsData

data class DueDateUiState(
    val pregnancyData: PregnancyDetailsData? = null,
    val estimatedDueDate: String = "13 Feb 2027",
    val selectedNavIndex: Int = 1,
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)
