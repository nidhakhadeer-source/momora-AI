package com.example.SIMATS.ui.bloodpressure

import com.example.SIMATS.data.model.BloodPressureData

data class BloodPressureUiState(
    val data: BloodPressureData? = null,
    val systolicInput: String = "112",
    val diastolicInput: String = "72",
    val selectedNavIndex: Int = 1,
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)
