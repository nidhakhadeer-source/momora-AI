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

    init {
        loadHistory("1")
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
            PatientHistoryUiEvent.OnBackClicked -> {}
            PatientHistoryUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadHistory(id: String) {
        viewModelScope.launch {
            repository.getPatientHistory(id).collect { data ->
                _uiState.value = _uiState.value.copy(historyData = data)
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
