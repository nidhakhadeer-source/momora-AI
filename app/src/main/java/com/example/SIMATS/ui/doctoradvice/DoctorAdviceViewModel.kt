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

    init {
        loadData("1")
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
            DoctorAdviceUiEvent.OnBackClicked -> {}
            DoctorAdviceUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadData(id: String) {
        viewModelScope.launch {
            repository.getAdviceData(id).collect { data ->
                _uiState.value = _uiState.value.copy(
                    adviceData = data,
                    subjectText = data.defaultSubject,
                    adviceText = data.defaultAdviceText
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
                _uiState.value.isSaveCopyChecked
            ).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isSending = false, isSentSuccess = true)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isSending = false, errorMessage = err.message)
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
