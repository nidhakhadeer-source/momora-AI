package com.example.SIMATS.ui.nutrition

import com.example.SIMATS.data.model.NutritionMealItem
import com.example.SIMATS.data.model.NutritionPlanData
import com.example.SIMATS.data.model.NutritionPriority

data class NutritionPlanUiState(
    val planData: NutritionPlanData? = null,
    val selectedNavIndex: Int = 3,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedMealType: String = "lunch",
    val selectedPriority: NutritionPriority? = null,
    val isMealSheetOpen: Boolean = false
)
