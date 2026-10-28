package com.example.SIMATS.ui.nutrition

sealed interface NutritionPlanUiEvent {
    data class OnNavTabSelected(val index: Int) : NutritionPlanUiEvent
    data object OnFeaturedMealClicked : NutritionPlanUiEvent
    data class OnPriorityClicked(val priorityId: Int) : NutritionPlanUiEvent
    data object OnBackClicked : NutritionPlanUiEvent
    data object OnNotificationClicked : NutritionPlanUiEvent
}
