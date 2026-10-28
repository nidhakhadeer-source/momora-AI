package com.example.SIMATS.ui.healthreport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.HealthMonitoringRepository
import com.example.SIMATS.data.repository.HealthMonitoringRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HealthReportViewModel(
    private val repository: HealthMonitoringRepository = HealthMonitoringRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HealthReportUiState())
    val uiState: StateFlow<HealthReportUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: HealthReportUiEvent) {
        when (event) {
            is HealthReportUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            HealthReportUiEvent.OnAddReadingClicked -> {}
            HealthReportUiEvent.OnWeightTrackerClicked -> {}
            HealthReportUiEvent.OnBloodPressureTrackerClicked -> {}
            HealthReportUiEvent.OnSymptomCheckerClicked -> {}
            HealthReportUiEvent.OnBackClicked -> {}
            HealthReportUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getHealthMonitoringData().collect { data ->
                _uiState.value = _uiState.value.copy(healthData = data)
            }
        }
    }

    class Factory(private val repository: HealthMonitoringRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HealthReportViewModel(repository) as T
        }
    }
}
