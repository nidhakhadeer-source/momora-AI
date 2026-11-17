package com.example.SIMATS.ui.patienthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.PatientHistoryRepository
import com.example.SIMATS.data.repository.PatientHistoryRepositoryImpl
import com.example.SIMATS.navigation.SelectedPatientHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientHistoryViewModel(
    private val repository: PatientHistoryRepository = PatientHistoryRepositoryImpl(),
    patientId: String? = SelectedPatientHolder.selectedPatientId,
    patientName: String? = SelectedPatientHolder.selectedPatientName
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PatientHistoryUiState(patientName = patientName ?: SelectedPatientHolder.selectedPatientName)
    )
    val uiState: StateFlow<PatientHistoryUiState> = _uiState.asStateFlow()

    private var currentPatientId: String? = patientId ?: SelectedPatientHolder.selectedPatientId

    init {
        val id = currentPatientId
        if (!id.isNullOrBlank()) {
            loadHistory(id)
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "No patient selected"
            )
        }
    }

    fun onEvent(event: PatientHistoryUiEvent) {
        when (event) {
            is PatientHistoryUiEvent.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTab = event.index)
            }
            is PatientHistoryUiEvent.OnMedicationClicked -> {
                _uiState.value = _uiState.value.copy(
                    selectedMedication = event.medication,
                    isMedicationSheetOpen = true
                )
            }
            PatientHistoryUiEvent.OnDismissMedicationSheet -> {
                _uiState.value = _uiState.value.copy(
                    isMedicationSheetOpen = false,
                    selectedMedication = null
                )
            }
            is PatientHistoryUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            PatientHistoryUiEvent.OnViewAllReportsClicked -> {
                _uiState.value = _uiState.value.copy(selectedTab = 1)
            }
            PatientHistoryUiEvent.OnOpenHealthMonitoringClicked -> {}
            PatientHistoryUiEvent.OnRetryClicked -> {
                val id = currentPatientId ?: SelectedPatientHolder.selectedPatientId
                if (!id.isNullOrBlank()) {
                    currentPatientId = id
                    loadHistory(id)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No patient selected"
                    )
                }
            }
            PatientHistoryUiEvent.OnBackClicked -> {}
            PatientHistoryUiEvent.OnNotificationClicked -> {}
        }
    }

    fun setPatientId(id: String, name: String? = null) {
        val resolvedName = name ?: SelectedPatientHolder.selectedPatientName
        if (id.isNotBlank() && (id != currentPatientId || _uiState.value.historyData == null)) {
            currentPatientId = id
            SelectedPatientHolder.selectedPatientId = id
            if (!resolvedName.isNullOrBlank()) {
                SelectedPatientHolder.selectedPatientName = resolvedName
            }
            _uiState.value = _uiState.value.copy(patientName = resolvedName ?: _uiState.value.patientName)
            loadHistory(id)
        }
    }

    private fun loadHistory(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientHistory(id).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        if (data.patientName.isNotBlank()) {
                            SelectedPatientHolder.selectedPatientName = data.patientName
                        }
                        _uiState.value = _uiState.value.copy(
                            historyData = data,
                            patientName = data.patientName.ifBlank { _uiState.value.patientName },
                            reportsCount = data.reports.size,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load patient history"
                        )
                    }
                )
            }
        }
    }

    class Factory(
        private val repository: PatientHistoryRepository = PatientHistoryRepositoryImpl(),
        private val patientId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientHistoryViewModel(repository, patientId) as T
        }
    }
}
