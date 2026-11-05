package com.example.SIMATS.ui.managedoctors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.ManageDoctorsRepository
import com.example.SIMATS.data.repository.ManageDoctorsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageDoctorsViewModel(
    private val repository: ManageDoctorsRepository = ManageDoctorsRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageDoctorsUiState())
    val uiState: StateFlow<ManageDoctorsUiState> = _uiState.asStateFlow()

    init {
        loadDoctors()
    }

    fun onEvent(event: ManageDoctorsUiEvent) {
        when (event) {
            is ManageDoctorsUiEvent.OnSearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is ManageDoctorsUiEvent.OnFilterTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedFilterTab = event.index)
            }
            is ManageDoctorsUiEvent.OnToggleDoctorStatusClicked -> {
                toggleStatus(event.doctorId)
            }
            is ManageDoctorsUiEvent.OnEditDetailsClicked -> {}
            is ManageDoctorsUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            ManageDoctorsUiEvent.OnAddDoctorClicked -> {}
            ManageDoctorsUiEvent.OnNextPageClicked -> {}
            ManageDoctorsUiEvent.OnBackClicked -> {}
            ManageDoctorsUiEvent.OnNotificationClicked -> {}
            ManageDoctorsUiEvent.OnRetryClicked -> {
                loadDoctors()
            }
        }
    }

    fun loadDoctors() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getDoctors().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val active = list.count { it.isActive }
                        val inactive = list.count { !it.isActive }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            doctors = list,
                            totalCount = list.size,
                            activeCount = active,
                            inactiveCount = inactive
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load doctors list"
                        )
                    }
                )
            }
        }
    }

    private fun toggleStatus(id: String) {
        viewModelScope.launch {
            repository.toggleDoctorStatus(id).collect { result ->
                result.onSuccess {
                    val updated = _uiState.value.doctors.map { doc ->
                        if (doc.id == id) {
                            val newActive = !doc.isActive
                            doc.copy(
                                isActive = newActive,
                                specialtyAndPatientsText = if (newActive) "Obstetrics · 18 assigned patients" else "Maternal medicine · Access disabled"
                            )
                        } else doc
                    }
                    val active = updated.count { it.isActive }
                    val inactive = updated.count { !it.isActive }
                    _uiState.value = _uiState.value.copy(
                        doctors = updated,
                        activeCount = active,
                        inactiveCount = inactive
                    )
                }
            }
        }
    }

    class Factory(private val repository: ManageDoctorsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManageDoctorsViewModel(repository) as T
        }
    }
}
