package com.example.SIMATS.ui.patientlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.PatientListRepository
import com.example.SIMATS.data.repository.PatientListRepositoryImpl
import com.example.SIMATS.navigation.SelectedPatientHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientListViewModel(
    private val repository: PatientListRepository = PatientListRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientListUiState())
    val uiState: StateFlow<PatientListUiState> = _uiState.asStateFlow()

    init {
        loadPatients(1)
    }

    fun onEvent(event: PatientListUiEvent) {
        when (event) {
            is PatientListUiEvent.OnSearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is PatientListUiEvent.OnFilterTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedFilterTab = event.index)
            }
            is PatientListUiEvent.OnPatientClicked -> {
                SelectedPatientHolder.selectedPatientId = event.patientId
                if (!event.patientName.isNullOrBlank()) {
                    SelectedPatientHolder.selectedPatientName = event.patientName
                }
            }
            is PatientListUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            PatientListUiEvent.OnPreviousPageClicked -> {
                if (_uiState.value.currentPage > 1) {
                    loadPatients(_uiState.value.currentPage - 1)
                }
            }
            PatientListUiEvent.OnNextPageClicked -> {
                if (_uiState.value.currentPage < _uiState.value.totalPages) {
                    loadPatients(_uiState.value.currentPage + 1)
                }
            }
            PatientListUiEvent.OnRetryClicked -> {
                loadPatients(_uiState.value.currentPage)
            }
            PatientListUiEvent.OnBackClicked -> {}
            PatientListUiEvent.OnNotificationClicked -> {}
        }
    }

    fun loadPatients(page: Int = _uiState.value.currentPage) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getAssignedPatients(page = page, limit = _uiState.value.limit).collect { result ->
                result.fold(
                    onSuccess = { paginated ->
                        val toReview = paginated.patients.count { it.needsReview }
                        _uiState.value = _uiState.value.copy(
                            patients = paginated.patients,
                            currentPage = paginated.page,
                            totalPages = paginated.totalPages,
                            totalCount = paginated.total,
                            reviewCount = toReview,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load assigned patients"
                        )
                    }
                )
            }
        }
    }

    class Factory(private val repository: PatientListRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientListViewModel(repository) as T
        }
    }
}
