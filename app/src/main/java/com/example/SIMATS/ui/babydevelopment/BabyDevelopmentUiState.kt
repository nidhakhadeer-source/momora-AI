package com.example.SIMATS.ui.babydevelopment

import com.example.SIMATS.data.model.BabyDevelopmentData

data class BabyDevelopmentUiState(
    val currentWeek: Int = 24,
    val developmentData: BabyDevelopmentData? = null,
    val selectedNavIndex: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
