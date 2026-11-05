package com.example.SIMATS.ui.doctoradvice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.DoctorAdviceRepository
import com.example.SIMATS.data.repository.DoctorAdviceRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorAdviceViewModel(
    private val repository: DoctorAdviceRepository = DoctorAdviceRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorAdviceUiState())
    val uiState: StateFlow<DoctorAdviceUiState> = _uiState.asStateFlow()

    private var currentPatientId: String = "1"

    init {
        loadData(currentPatientId)
    }

    fun onEvent(event: DoctorAdviceUiEvent) {
        when (event) {
            is DoctorAdviceUiEvent.OnSubjectChanged -> {
                _uiState.value = _uiState.value.copy(subjectText = event.subject)
            }
            is DoctorAdviceUiEvent.OnAdviceTextChanged -> {
                _uiState.value = _uiState.value.copy(adviceText = event.text)
            }
            is DoctorAdviceUiEvent.OnSaveCopyToggled -> {
                _uiState.value = _uiState.value.copy(isSaveCopyChecked = event.isChecked)
            }
            is DoctorAdviceUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            DoctorAdviceUiEvent.OnSendAdviceClicked -> {
                sendAdvice()
            }
            DoctorAdviceUiEvent.OnRetryClicked -> {
                loadData(currentPatientId)
            }
            DoctorAdviceUiEvent.OnBackClicked -> {}
            DoctorAdviceUiEvent.OnNotificationClicked -> {}
        }
    }

    fun setPatientId(id: String) {
        currentPatientId = id
        loadData(id)
    }

    private fun loadData(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getAdviceData(id).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _uiState.value = _uiState.value.copy(
                            adviceData = data,
                            subjectText = data.defaultSubject,
                            adviceText = data.defaultAdviceText,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load doctor advice data"
                        )
                    }
                )
            }
        }
    }

    private fun sendAdvice() {
        _uiState.value = _uiState.value.copy(isSending = true, errorMessage = null)
        viewModelScope.launch {
            repository.sendAdvice(
                _uiState.value.subjectText,
                _uiState.value.adviceText,
                _uiState.value.isSaveCopyChecked,
                currentPatientId
            ).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isSending = false, isSentSuccess = true, errorMessage = null)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isSending = false, errorMessage = err.message ?: "Failed to send advice")
                }
            }
        }
    }

    class Factory(private val repository: DoctorAdviceRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DoctorAdviceViewModel(repository) as T
        }
    }
}
