package com.example.SIMATS.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.DashboardRepository
import com.example.SIMATS.data.repository.DashboardRepositoryImpl
import com.example.SIMATS.data.repository.PregnancyRepository
import com.example.SIMATS.data.repository.PregnancyRepositoryImpl
import com.example.SIMATS.ui.duedate.PregnancyCalculator
import com.example.SIMATS.ui.duedate.PregnancyDataHolder
import com.example.SIMATS.ui.duedate.PregnancyMetrics
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: DashboardRepository = DashboardRepositoryImpl(),
    private val pregnancyRepository: PregnancyRepository = PregnancyRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        observeSharedPregnancy()
    }

    private fun observeSharedPregnancy() {
        viewModelScope.launch {
            PregnancyDataHolder.pregnancyMetrics.collect { metrics ->
                if (metrics != null) {
                    applyMetricsToDashboard(metrics)
                }
            }
        }
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
            HomeUiEvent.OnMedicationClicked -> {}
            HomeUiEvent.OnAppointmentClicked -> {}
            HomeUiEvent.OnDoctorAdviceClicked -> {}
            HomeUiEvent.OnRetryClicked -> {
                loadDashboardData()
            }
        }
    }

    private fun loadDashboardData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            // Also refresh authoritative pregnancy details from PregnancyRepository in parallel
            launch {
                pregnancyRepository.getPregnancyDetails().collect { result ->
                    result.onSuccess { details ->
                        val metrics = PregnancyDataHolder.updatePregnancyData(details)
                        applyMetricsToDashboard(metrics)
                    }
                }
            }

            repository.getPatientDashboardData().collect { result ->
                result.fold(
                    onSuccess = { dashboardData ->
                        val metrics = PregnancyDataHolder.getOrComputeMetrics()
                            ?: PregnancyCalculator.computeFromDashboardData(dashboardData)
                        val syncedData = if (metrics != null) {
                            dashboardData.copy(
                                trimesterText = metrics.trimesterText,
                                gestationalAgeText = metrics.gestationalAgeText,
                                dueDateText = "Due ${metrics.eddFormatted} · Pregnancy details ➔",
                                progressPercent = metrics.progress
                            )
                        } else {
                            dashboardData
                        }
                        _uiState.value = _uiState.value.copy(data = syncedData, isLoading = false, errorMessage = null)
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message ?: "Failed to load dashboard data")
                    }
                )
            }
        }
    }

    private fun applyMetricsToDashboard(metrics: PregnancyMetrics) {
        val currentData = _uiState.value.data ?: return
        _uiState.value = _uiState.value.copy(
            data = currentData.copy(
                trimesterText = metrics.trimesterText,
                gestationalAgeText = metrics.gestationalAgeText,
                dueDateText = "Due ${metrics.eddFormatted} · Pregnancy details ➔",
                progressPercent = metrics.progress
            )
        )
    }

    class Factory(
        private val repository: DashboardRepository,
        private val pregnancyRepository: PregnancyRepository = PregnancyRepositoryImpl()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository, pregnancyRepository) as T
        }
    }
}
