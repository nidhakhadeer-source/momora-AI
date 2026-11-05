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
            ManagePatientsUiEvent.OnRetryClicked -> {
                loadPatients()
            }
        }
    }

    fun loadPatients() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientAccounts().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val active = list.count { it.isActive }
                        val inactive = list.count { !it.isActive }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            patients = list,
                            totalCount = list.size,
                            activeCount = active,
                            inactiveCount = inactive
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load patient accounts"
                        )
                    }
                )
            }
        }
    }

    private fun toggleAccess(id: String) {
        viewModelScope.launch {
            repository.togglePatientAccess(id).collect { result ->
                result.onSuccess {
                    val updated = _uiState.value.patients.map { p ->
                        if (p.id == id) {
                            val newActive = !p.isActive
                            p.copy(
                                isActive = newActive,
                                assignedDoctorText = if (newActive) "Assigned doctor: Dr Amelia Chen" else "Account access disabled"
                            )
                        } else p
                    }
                    val active = updated.count { it.isActive }
                    val inactive = updated.count { !it.isActive }
                    _uiState.value = _uiState.value.copy(
                        patients = updated,
                        activeCount = active,
                        inactiveCount = inactive
                    )
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
