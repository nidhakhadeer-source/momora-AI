package com.example.SIMATS.ui.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.NutritionRepository
import com.example.SIMATS.data.repository.NutritionRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NutritionPlanViewModel(
    private val repository: NutritionRepository = NutritionRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(NutritionPlanUiState())
    val uiState: StateFlow<NutritionPlanUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: NutritionPlanUiEvent) {
        when (event) {
            is NutritionPlanUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            NutritionPlanUiEvent.OnRetryClicked -> {
                loadData()
            }
            NutritionPlanUiEvent.OnFeaturedMealClicked -> {
                _uiState.value = _uiState.value.copy(isMealSheetOpen = true)
            }
            is NutritionPlanUiEvent.OnPriorityClicked -> {
                val priority = _uiState.value.planData?.priorities?.find { it.id == event.priorityId }
                _uiState.value = _uiState.value.copy(selectedPriority = priority)
            }
            is NutritionPlanUiEvent.OnSelectMealType -> {
                _uiState.value = _uiState.value.copy(selectedMealType = event.mealType.lowercase())
            }
            NutritionPlanUiEvent.OnDismissPrioritySheet -> {
                _uiState.value = _uiState.value.copy(selectedPriority = null)
            }
            NutritionPlanUiEvent.OnDismissMealSheet -> {
                _uiState.value = _uiState.value.copy(isMealSheetOpen = false)
            }
            NutritionPlanUiEvent.OnBackClicked -> {}
            NutritionPlanUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getNutritionPlan().collect { result ->
                result.onSuccess { data ->
                    val initialMealType = determineInitialMealType(data.featuredMealCategory)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        planData = data,
                        selectedMealType = initialMealType,
                        errorMessage = null
                    )
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Failed to load nutrition plan"
                    )
                }
            }
        }
    }

    private fun determineInitialMealType(category: String): String {
        val lower = category.lowercase()
        return when {
            lower.contains("breakfast") -> "breakfast"
            lower.contains("dinner") -> "dinner"
            lower.contains("snack") -> "snack"
            else -> "lunch"
        }
    }

    class Factory(private val repository: NutritionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NutritionPlanViewModel(repository) as T
        }
    }
}
