package com.example.SIMATS.ui.symptom

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.SymptomEntryData
import com.example.SIMATS.data.repository.SymptomRepository
import com.example.SIMATS.data.repository.SymptomRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SymptomLoggerViewModel(
    private val repository: SymptomRepository = SymptomRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SymptomLoggerUiState())
    val uiState: StateFlow<SymptomLoggerUiState> = _uiState.asStateFlow()

    init {
        loadSymptomData()
    }

    fun onEvent(event: SymptomLoggerUiEvent) {
        when (event) {
            is SymptomLoggerUiEvent.OnSymptomSelected -> {
                _uiState.value = _uiState.value.copy(
                    selectedSymptom = event.symptom,
                    isDropdownExpanded = false
                )
            }
            is SymptomLoggerUiEvent.OnSeveritySelected -> {
                _uiState.value = _uiState.value.copy(selectedSeverity = event.severity)
            }
            is SymptomLoggerUiEvent.OnStartedChanged -> {
                _uiState.value = _uiState.value.copy(startedText = event.started)
            }
            is SymptomLoggerUiEvent.OnDurationChanged -> {
                _uiState.value = _uiState.value.copy(durationText = event.duration)
            }
            is SymptomLoggerUiEvent.OnDropdownToggled -> {
                _uiState.value = _uiState.value.copy(isDropdownExpanded = event.expanded)
            }
            is SymptomLoggerUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            SymptomLoggerUiEvent.OnSaveSymptomClicked -> {
                saveSymptom()
            }
            SymptomLoggerUiEvent.OnBackClicked -> {}
            SymptomLoggerUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadSymptomData() {
        viewModelScope.launch {
            repository.getSymptomEntryData().collect { data ->
                _uiState.value = _uiState.value.copy(
                    entryData = data,
                    selectedSymptom = data.symptomName,
                    selectedSeverity = data.severity,
                    startedText = data.startedText,
                    durationText = data.durationText
                )
            }
        }
    }

    private fun saveSymptom() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val entryData = SymptomEntryData(
                symptomName = _uiState.value.selectedSymptom,
                severity = _uiState.value.selectedSeverity,
                startedText = _uiState.value.startedText,
                durationText = _uiState.value.durationText,
                aiGuidanceText = _uiState.value.entryData?.aiGuidanceText ?: "",
                availableSymptoms = _uiState.value.entryData?.availableSymptoms ?: emptyList()
            )
            repository.saveSymptom(entryData).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSavedSuccess = true)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
                }
            }
        }
    }

    class Factory(private val repository: SymptomRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SymptomLoggerViewModel(repository) as T
        }
    }
}
