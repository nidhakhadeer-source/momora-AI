package com.example.SIMATS.ui.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.WeightRepository
import com.example.SIMATS.data.repository.WeightRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeightTrackerViewModel(
    private val repository: WeightRepository = WeightRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeightTrackerUiState())
    val uiState: StateFlow<WeightTrackerUiState> = _uiState.asStateFlow()

    init {
        loadWeightData()
    }

    fun onEvent(event: WeightTrackerUiEvent) {
        when (event) {
            is WeightTrackerUiEvent.OnTimeRangeSelected -> {
                _uiState.value = _uiState.value.copy(selectedTimeRangeIndex = event.index)
            }
            is WeightTrackerUiEvent.OnWeightInputChanged -> {
                _uiState.value = _uiState.value.copy(inputWeight = event.weight)
            }
            is WeightTrackerUiEvent.OnDateInputChanged -> {
                _uiState.value = _uiState.value.copy(inputDate = event.date)
            }
            is WeightTrackerUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            WeightTrackerUiEvent.OnSaveReadingClicked -> {
                saveReading()
            }
            WeightTrackerUiEvent.OnRetryClicked -> {
                loadWeightData()
            }
            WeightTrackerUiEvent.OnBackClicked -> {}
            WeightTrackerUiEvent.OnNotificationClicked -> {}
        }
    }

    fun loadWeightData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getWeightTrackerData().collect { result ->
                result.fold(
                    onSuccess = { trackerData ->
                        _uiState.value = _uiState.value.copy(
                            data = trackerData,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load weight data"
                        )
                    }
                )
            }
        }
    }

    private fun saveReading() {
        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
        viewModelScope.launch {
            repository.saveWeightReading(_uiState.value.inputWeight, _uiState.value.inputDate)
                .collect { result ->
                    result.onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            isSavedSuccess = true
                        )
                        loadWeightData()
                    }.onFailure { err ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = err.message ?: "Failed to save weight reading"
                        )
                    }
                }
        }
    }

    class Factory(private val repository: WeightRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return WeightTrackerViewModel(repository) as T
        }
    }
}
