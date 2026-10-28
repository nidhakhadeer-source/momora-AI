package com.example.SIMATS.ui.nutrition

import com.example.SIMATS.data.model.NutritionPlanData

data class NutritionPlanUiState(
    val planData: NutritionPlanData? = null,
    val selectedNavIndex: Int = 3,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
