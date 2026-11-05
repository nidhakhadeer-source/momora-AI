package com.example.SIMATS.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.DashboardRepository
import com.example.SIMATS.data.repository.DashboardRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: DashboardRepository = DashboardRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            is HomeUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            HomeUiEvent.OnNotificationClicked -> {}
            HomeUiEvent.OnPregnancyDetailsClicked -> {}
            HomeUiEvent.OnBloodPressureCardClicked -> {}
            HomeUiEvent.OnWeightCardClicked -> {}
            HomeUiEvent.OnAskAiClicked -> {}
            HomeUiEvent.OnBabyGrowthClicked -> {}
            HomeUiEvent.OnNutritionClicked -> {}
            HomeUiEvent.OnViewAllDailyCareClicked -> {}
            HomeUiEvent.OnRetryClicked -> {
                loadDashboardData()
            }
        }
    }

    private fun loadDashboardData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientDashboardData().collect { result ->
                result.fold(
                    onSuccess = { dashboardData ->
                        _uiState.value = _uiState.value.copy(data = dashboardData, isLoading = false, errorMessage = null)
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Failed to load dashboard data")
                    }
                )
            }
        }
    }

    class Factory(private val repository: DashboardRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
