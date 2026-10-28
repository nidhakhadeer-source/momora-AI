package com.example.SIMATS.ui.weight

import com.example.SIMATS.data.model.WeightTrackerData

data class WeightTrackerUiState(
    val data: WeightTrackerData? = null,
    val selectedTimeRangeIndex: Int = 1,
    val inputWeight: String = "64.2",
    val inputDate: String = "27 Oct 2026",
    val selectedNavIndex: Int = 1,
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)
