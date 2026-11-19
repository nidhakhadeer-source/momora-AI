package com.example.SIMATS.ui.nutrition

sealed interface NutritionPlanUiEvent {
    data class OnNavTabSelected(val index: Int) : NutritionPlanUiEvent
    data object OnFeaturedMealClicked : NutritionPlanUiEvent
    data class OnPriorityClicked(val priorityId: Int) : NutritionPlanUiEvent
    data class OnSelectMealType(val mealType: String) : NutritionPlanUiEvent
    data object OnDismissPrioritySheet : NutritionPlanUiEvent
    data object OnDismissMealSheet : NutritionPlanUiEvent
    data object OnBackClicked : NutritionPlanUiEvent
    data object OnNotificationClicked : NutritionPlanUiEvent
    data object OnRetryClicked : NutritionPlanUiEvent
}
