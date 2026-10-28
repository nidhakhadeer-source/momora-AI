package com.example.SIMATS.ui.patienthealthmonitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.PatientHealthMonitoringRepository
import com.example.SIMATS.data.repository.PatientHealthMonitoringRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientHealthMonitoringViewModel(
    private val repository: PatientHealthMonitoringRepository = PatientHealthMonitoringRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientHealthMonitoringUiState())
    val uiState: StateFlow<PatientHealthMonitoringUiState> = _uiState.asStateFlow()

    init {
        loadMonitoring("1")
    }

    fun onEvent(event: PatientHealthMonitoringUiEvent) {
        when (event) {
            is PatientHealthMonitoringUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            PatientHealthMonitoringUiEvent.OnWriteAdviceClicked -> {}
            PatientHealthMonitoringUiEvent.OnBackClicked -> {}
            PatientHealthMonitoringUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadMonitoring(id: String) {
        viewModelScope.launch {
            repository.getPatientHealthMonitoring(id).collect { data ->
                _uiState.value = _uiState.value.copy(monitoringData = data)
            }
        }
    }

    class Factory(private val repository: PatientHealthMonitoringRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientHealthMonitoringViewModel(repository) as T
        }
    }
}
