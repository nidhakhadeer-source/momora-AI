package com.example.SIMATS.ui.nutrition

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class NutritionPlanViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionPlanUiState())
    val uiState: StateFlow<NutritionPlanUiState> = _uiState.asStateFlow()

    fun onEvent(event: NutritionPlanUiEvent) {
        when (event) {
            NutritionPlanUiEvent.OnToggleBookmark -> {
                _uiState.update { it.copy(isBookmarked = !it.isBookmarked) }
            }
            NutritionPlanUiEvent.OnSwapMealsClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ExerciseGuidance.route) }
            }
            is NutritionPlanUiEvent.OnMealActionClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ExerciseGuidance.route) }
            }
            NutritionPlanUiEvent.OnOpenShoppingListClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ExerciseGuidance.route) }
            }
            is NutritionPlanUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            NutritionPlanUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            NutritionPlanUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
