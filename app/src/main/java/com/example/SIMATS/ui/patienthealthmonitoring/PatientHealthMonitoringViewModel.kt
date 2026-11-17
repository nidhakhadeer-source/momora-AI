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
    patientId: String? = SelectedPatientHolder.selectedPatientId,
    patientName: String? = SelectedPatientHolder.selectedPatientName
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PatientHealthMonitoringUiState(patientName = patientName ?: SelectedPatientHolder.selectedPatientName)
    )
    val uiState: StateFlow<PatientHealthMonitoringUiState> = _uiState.asStateFlow()

    private var currentPatientId: String? = patientId ?: SelectedPatientHolder.selectedPatientId

    init {
        val id = currentPatientId
        if (!id.isNullOrBlank()) {
            loadMonitoring(id, _uiState.value.selectedDays)
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
            is PatientHealthMonitoringUiEvent.OnDaysSelected -> {
                _uiState.value = _uiState.value.copy(selectedDays = event.days)
                val id = currentPatientId ?: SelectedPatientHolder.selectedPatientId
                if (!id.isNullOrBlank()) {
                    loadMonitoring(id, event.days)
                }
            }
            PatientHealthMonitoringUiEvent.OnBpCardClicked -> {
                _uiState.value = _uiState.value.copy(isBpSheetOpen = true, isWeightSheetOpen = false)
            }
            PatientHealthMonitoringUiEvent.OnWeightCardClicked -> {
                _uiState.value = _uiState.value.copy(isWeightSheetOpen = true, isBpSheetOpen = false)
            }
            PatientHealthMonitoringUiEvent.OnDismissSheet -> {
                _uiState.value = _uiState.value.copy(isBpSheetOpen = false, isWeightSheetOpen = false)
            }
            PatientHealthMonitoringUiEvent.OnWriteAdviceClicked -> {}
            PatientHealthMonitoringUiEvent.OnRetryClicked -> {
                val id = currentPatientId ?: SelectedPatientHolder.selectedPatientId
                if (!id.isNullOrBlank()) {
                    currentPatientId = id
                    loadMonitoring(id, _uiState.value.selectedDays)
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

    fun setPatientId(id: String, name: String? = null) {
        val resolvedName = name ?: SelectedPatientHolder.selectedPatientName
        if (id.isNotBlank() && (id != currentPatientId || _uiState.value.monitoringData == null)) {
            currentPatientId = id
            SelectedPatientHolder.selectedPatientId = id
            if (!resolvedName.isNullOrBlank()) {
                SelectedPatientHolder.selectedPatientName = resolvedName
            }
            _uiState.value = _uiState.value.copy(patientName = resolvedName ?: _uiState.value.patientName)
            loadMonitoring(id, _uiState.value.selectedDays)
        }
    }

    private fun loadMonitoring(id: String, days: Int = _uiState.value.selectedDays) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientHealthMonitoring(id, days).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        if (data.patientName.isNotBlank()) {
                            SelectedPatientHolder.selectedPatientName = data.patientName
                        }
                        _uiState.value = _uiState.value.copy(
                            monitoringData = data,
                            patientName = data.patientName.ifBlank { _uiState.value.patientName },
                            selectedDays = days,
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
