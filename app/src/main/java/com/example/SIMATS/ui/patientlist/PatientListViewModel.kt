package com.example.SIMATS.ui.patientlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.PatientListRepository
import com.example.SIMATS.data.repository.PatientListRepositoryImpl
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
        loadPatients()
    }

    fun onEvent(event: PatientListUiEvent) {
        when (event) {
            is PatientListUiEvent.OnSearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is PatientListUiEvent.OnFilterTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedFilterTab = event.index)
            }
            is PatientListUiEvent.OnPatientClicked -> {}
            is PatientListUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            PatientListUiEvent.OnPreviousPageClicked -> {}
            PatientListUiEvent.OnNextPageClicked -> {}
            PatientListUiEvent.OnBackClicked -> {}
            PatientListUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadPatients() {
        viewModelScope.launch {
            repository.getAssignedPatients().collect { list ->
                val toReview = list.count { it.needsReview }
                _uiState.value = _uiState.value.copy(
                    patients = list,
                    reviewCount = toReview
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
