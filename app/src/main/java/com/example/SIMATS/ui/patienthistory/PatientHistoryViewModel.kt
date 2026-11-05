package com.example.SIMATS.ui.patienthistory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.PatientHistoryRepository
import com.example.SIMATS.data.repository.PatientHistoryRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientHistoryViewModel(
    private val repository: PatientHistoryRepository = PatientHistoryRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientHistoryUiState())
    val uiState: StateFlow<PatientHistoryUiState> = _uiState.asStateFlow()

    private var currentPatientId: String = "1"

    init {
        loadHistory(currentPatientId)
    }

    fun onEvent(event: PatientHistoryUiEvent) {
        when (event) {
            is PatientHistoryUiEvent.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTab = event.index)
            }
            is PatientHistoryUiEvent.OnMedicationClicked -> {}
            is PatientHistoryUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            PatientHistoryUiEvent.OnViewAllReportsClicked -> {}
            PatientHistoryUiEvent.OnOpenHealthMonitoringClicked -> {}
            PatientHistoryUiEvent.OnRetryClicked -> {
                loadHistory(currentPatientId)
            }
            PatientHistoryUiEvent.OnBackClicked -> {}
            PatientHistoryUiEvent.OnNotificationClicked -> {}
        }
    }

    fun setPatientId(id: String) {
        currentPatientId = id
        loadHistory(id)
    }

    private fun loadHistory(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientHistory(id).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _uiState.value = _uiState.value.copy(
                            historyData = data,
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

    class Factory(private val repository: PatientHistoryRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientHistoryViewModel(repository) as T
        }
    }
}
