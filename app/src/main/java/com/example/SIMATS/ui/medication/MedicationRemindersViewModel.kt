package com.example.SIMATS.ui.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.MedicationRepository
import com.example.SIMATS.data.repository.MedicationRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicationRemindersViewModel(
    private val repository: MedicationRepository = MedicationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationRemindersUiState())
    val uiState: StateFlow<MedicationRemindersUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: MedicationRemindersUiEvent) {
        when (event) {
            is MedicationRemindersUiEvent.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTab = event.index)
            }
            is MedicationRemindersUiEvent.OnMarkAsTakenClicked -> {
                markTaken(event.medicationId)
            }
            is MedicationRemindersUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            MedicationRemindersUiEvent.OnAddMedicationClicked -> {}
            MedicationRemindersUiEvent.OnSettingsClicked -> {}
            MedicationRemindersUiEvent.OnBackClicked -> {}
            MedicationRemindersUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getMedicationReminders().collect { data ->
                _uiState.value = _uiState.value.copy(data = data)
            }
        }
    }

    private fun markTaken(id: Int) {
        viewModelScope.launch {
            repository.markAsTaken(id).collect { result ->
                result.onSuccess {
                    val currentData = _uiState.value.data
                    if (currentData != null) {
                        val updatedList = currentData.medications.map { med ->
                            if (med.id == id) {
                                med.copy(
                                    isTaken = true,
                                    takenTimeNote = "Marked taken today at 20:00"
                                )
                            } else med
                        }
                        _uiState.value = _uiState.value.copy(
                            data = currentData.copy(
                                progressStatusText = "2 OF 2 TAKEN · ALL COMPLETED",
                                medications = updatedList
                            )
                        )
                    }
                }
            }
        }
    }

    class Factory(private val repository: MedicationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MedicationRemindersViewModel(repository) as T
        }
    }
}
