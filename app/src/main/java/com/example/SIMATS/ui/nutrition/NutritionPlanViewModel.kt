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
            NutritionPlanUiEvent.OnFeaturedMealClicked -> {}
            is NutritionPlanUiEvent.OnPriorityClicked -> {}
            NutritionPlanUiEvent.OnBackClicked -> {}
            NutritionPlanUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getNutritionPlan().collect { data ->
                _uiState.value = _uiState.value.copy(planData = data)
            }
        }
    }

    class Factory(private val repository: NutritionRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NutritionPlanViewModel(repository) as T
        }
    }
}
