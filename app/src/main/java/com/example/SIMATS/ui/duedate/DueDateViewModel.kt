package com.example.SIMATS.ui.duedate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.PregnancyDetailsData
import com.example.SIMATS.data.repository.PregnancyRepository
import com.example.SIMATS.data.repository.PregnancyRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DueDateViewModel(
    private val repository: PregnancyRepository = PregnancyRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DueDateUiState())
    val uiState: StateFlow<DueDateUiState> = _uiState.asStateFlow()

    init {
        loadPregnancyDetails()
    }

    fun onEvent(event: DueDateUiEvent) {
        when (event) {
            is DueDateUiEvent.OnDueDateChanged -> {
                _uiState.value = _uiState.value.copy(estimatedDueDate = event.dueDate)
            }
            is DueDateUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            DueDateUiEvent.OnSaveClicked -> {
                saveDetails()
            }
            DueDateUiEvent.OnBackClicked -> {}
            DueDateUiEvent.OnNotificationClicked -> {}
            DueDateUiEvent.OnDoctorCardClicked -> {}
            DueDateUiEvent.OnNextVisitClicked -> {}
        }
    }

    private fun loadPregnancyDetails() {
        viewModelScope.launch {
            repository.getPregnancyDetails().collect { data ->
                _uiState.value = _uiState.value.copy(
                    pregnancyData = data,
                    estimatedDueDate = data.estimatedDueDate
                )
            }
        }
    }

    private fun saveDetails() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val updated = _uiState.value.pregnancyData?.copy(
                estimatedDueDate = _uiState.value.estimatedDueDate
            ) ?: PregnancyDetailsData(
                trimesterText = "SECOND TRIMESTER",
                gestationalAgeText = "24 weeks, 3 days",
                countdownText = "15 weeks, 4 days until your estimated due date.",
                estimatedDueDate = _uiState.value.estimatedDueDate,
                lastMenstrualPeriod = "09 May 2026",
                assignedDoctorName = "Dr Amelia Chen",
                assignedDoctorSpecialty = "Obstetrics · Your assigned doctor",
                nextVisitDate = "29 Oct 2026 · 10:30",
                nextVisitClinic = "Willow Clinic"
            )

            repository.savePregnancyDetails(updated).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSavedSuccess = true)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
                }
            }
        }
    }

    class Factory(private val repository: PregnancyRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DueDateViewModel(repository) as T
        }
    }
}
