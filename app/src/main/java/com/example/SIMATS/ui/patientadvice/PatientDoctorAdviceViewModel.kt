package com.example.SIMATS.ui.patientadvice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.PatientAdviceRepository
import com.example.SIMATS.data.repository.PatientAdviceRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PatientDoctorAdviceViewModel(
    private val repository: PatientAdviceRepository = PatientAdviceRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PatientDoctorAdviceUiState(isLoading = true))
    val uiState: StateFlow<PatientDoctorAdviceUiState> = _uiState.asStateFlow()

    init {
        loadAdvice()
    }

    fun onEvent(event: PatientDoctorAdviceUiEvent) {
        when (event) {
            PatientDoctorAdviceUiEvent.OnScreenOpened,
            PatientDoctorAdviceUiEvent.OnRetryClicked -> {
                loadAdvice()
            }
            PatientDoctorAdviceUiEvent.OnBackClicked -> {}
            PatientDoctorAdviceUiEvent.OnNotificationClicked -> {}
            is PatientDoctorAdviceUiEvent.OnNavTabSelected -> {}
        }
    }

    fun loadAdvice() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientAdvice().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        _uiState.value = _uiState.value.copy(
                            adviceList = list,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load doctor advice"
                        )
                    }
                )
            }
        }
    }

    class Factory(private val repository: PatientAdviceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientDoctorAdviceViewModel(repository) as T
        }
    }
}
