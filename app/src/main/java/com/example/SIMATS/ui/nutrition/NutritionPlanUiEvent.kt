package com.example.SIMATS.ui.nutrition

sealed interface NutritionPlanUiEvent {
    data object OnToggleBookmark : NutritionPlanUiEvent
    data object OnSwapMealsClicked : NutritionPlanUiEvent
    data class OnMealActionClicked(val mealId: String) : NutritionPlanUiEvent
    data object OnOpenShoppingListClicked : NutritionPlanUiEvent
    data class OnTabSelected(val tabIndex: Int) : NutritionPlanUiEvent
    data object OnBackClicked : NutritionPlanUiEvent
    data object OnNavigationHandled : NutritionPlanUiEvent
}
