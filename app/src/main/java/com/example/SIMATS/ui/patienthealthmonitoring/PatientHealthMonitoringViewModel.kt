package com.example.SIMATS.ui.patienthealthmonitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.PatientHealthMonitoringRepository
import com.example.SIMATS.data.repository.PatientHealthMonitoringRepositoryImpl
import com.example.SIMATS.navigation.SelectedPatientHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientHealthMonitoringViewModel(
    private val repository: PatientHealthMonitoringRepository = PatientHealthMonitoringRepositoryImpl(),
    patientId: String? = SelectedPatientHolder.selectedPatientId
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientHealthMonitoringUiState())
    val uiState: StateFlow<PatientHealthMonitoringUiState> = _uiState.asStateFlow()

    private var currentPatientId: String? = patientId ?: SelectedPatientHolder.selectedPatientId

    init {
        val id = currentPatientId
        if (!id.isNullOrBlank()) {
            loadMonitoring(id)
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "No patient selected"
            )
        }
    }

    fun onEvent(event: PatientHealthMonitoringUiEvent) {
        when (event) {
            is PatientHealthMonitoringUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            PatientHealthMonitoringUiEvent.OnWriteAdviceClicked -> {}
            PatientHealthMonitoringUiEvent.OnRetryClicked -> {
                val id = currentPatientId ?: SelectedPatientHolder.selectedPatientId
                if (!id.isNullOrBlank()) {
                    currentPatientId = id
                    loadMonitoring(id)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No patient selected"
                    )
                }
            }
            PatientHealthMonitoringUiEvent.OnBackClicked -> {}
            PatientHealthMonitoringUiEvent.OnNotificationClicked -> {}
        }
    }

    fun setPatientId(id: String) {
        if (id.isNotBlank() && (id != currentPatientId || _uiState.value.monitoringData == null)) {
            currentPatientId = id
            SelectedPatientHolder.selectedPatientId = id
            loadMonitoring(id)
        }
    }

    private fun loadMonitoring(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientHealthMonitoring(id).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _uiState.value = _uiState.value.copy(
                            monitoringData = data,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load health monitoring data"
                        )
                    }
                )
            }
        }
    }

    class Factory(
        private val repository: PatientHealthMonitoringRepository = PatientHealthMonitoringRepositoryImpl(),
        private val patientId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientHealthMonitoringViewModel(repository, patientId) as T
        }
    }
}
