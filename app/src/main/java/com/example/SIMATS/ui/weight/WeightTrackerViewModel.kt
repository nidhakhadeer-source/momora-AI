package com.example.SIMATS.ui.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.WeightTrackerData
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
            WeightTrackerUiEvent.OnBackClicked -> {}
            WeightTrackerUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadWeightData() {
        viewModelScope.launch {
            repository.getWeightTrackerData().collect { trackerData ->
                _uiState.value = _uiState.value.copy(data = trackerData)
            }
        }
    }

    private fun saveReading() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.saveWeightReading(_uiState.value.inputWeight, _uiState.value.inputDate)
                .collect { result ->
                    result.onSuccess {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSavedSuccess = true,
                            data = _uiState.value.data?.copy(
                                currentWeightKg = _uiState.value.inputWeight
                            )
                        )
                    }.onFailure { err ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
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
