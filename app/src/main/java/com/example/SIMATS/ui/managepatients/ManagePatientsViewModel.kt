package com.example.SIMATS.ui.managepatients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.ManagePatientsRepository
import com.example.SIMATS.data.repository.ManagePatientsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManagePatientsViewModel(
    private val repository: ManagePatientsRepository = ManagePatientsRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagePatientsUiState())
    val uiState: StateFlow<ManagePatientsUiState> = _uiState.asStateFlow()

    init {
        loadPatients()
    }

    fun onEvent(event: ManagePatientsUiEvent) {
        when (event) {
            is ManagePatientsUiEvent.OnSearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is ManagePatientsUiEvent.OnFilterTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedFilterTab = event.index)
            }
            is ManagePatientsUiEvent.OnEditAccountClicked -> {}
            is ManagePatientsUiEvent.OnManageAccessClicked -> {
                toggleAccess(event.patientId)
            }
            is ManagePatientsUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            ManagePatientsUiEvent.OnNextPageClicked -> {}
            ManagePatientsUiEvent.OnBackClicked -> {}
            ManagePatientsUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadPatients() {
        viewModelScope.launch {
            repository.getPatientAccounts().collect { list ->
                _uiState.value = _uiState.value.copy(patients = list)
            }
        }
    }

    private fun toggleAccess(id: String) {
        viewModelScope.launch {
            repository.togglePatientAccess(id).collect { result ->
                result.onSuccess {
                    val updated = _uiState.value.patients.map { p ->
                        if (p.id == id) {
                            p.copy(
                                isActive = !p.isActive,
                                assignedDoctorText = if (p.isActive) "Account access disabled" else "Assigned doctor: Dr Amelia Chen"
                            )
                        } else p
                    }
                    _uiState.value = _uiState.value.copy(patients = updated)
                }
            }
        }
    }

    class Factory(private val repository: ManagePatientsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManagePatientsViewModel(repository) as T
        }
    }
}
