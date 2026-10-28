package com.example.SIMATS.ui.bloodpressure

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.BloodPressureData
import com.example.SIMATS.data.repository.BloodPressureRepository
import com.example.SIMATS.data.repository.BloodPressureRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BloodPressureViewModel(
    private val repository: BloodPressureRepository = BloodPressureRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BloodPressureUiState())
    val uiState: StateFlow<BloodPressureUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: BloodPressureUiEvent) {
        when (event) {
            is BloodPressureUiEvent.OnSystolicInputChanged -> {
                _uiState.value = _uiState.value.copy(systolicInput = event.value)
            }
            is BloodPressureUiEvent.OnDiastolicInputChanged -> {
                _uiState.value = _uiState.value.copy(diastolicInput = event.value)
            }
            is BloodPressureUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            BloodPressureUiEvent.OnSaveReadingClicked -> {
                saveReading()
            }
            BloodPressureUiEvent.OnBackClicked -> {}
            BloodPressureUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getBloodPressureData().collect { bpData ->
                _uiState.value = _uiState.value.copy(data = bpData)
            }
        }
    }

    private fun saveReading() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.saveBloodPressureReading(_uiState.value.systolicInput, _uiState.value.diastolicInput)
                .collect { result ->
                    result.onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSavedSuccess = true,
                            data = _uiState.value.data?.copy(
                                currentReadingText = "${_uiState.value.systolicInput} / ${_uiState.value.diastolicInput} mmHg"
                            )
                        )
                    }.onFailure { err ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
                    }
                }
        }
    }

    class Factory(private val repository: BloodPressureRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BloodPressureViewModel(repository) as T
        }
    }
}
