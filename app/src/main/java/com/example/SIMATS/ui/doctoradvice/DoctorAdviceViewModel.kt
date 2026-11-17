package com.example.SIMATS.ui.doctoradvice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.DoctorAdviceRepository
import com.example.SIMATS.data.repository.DoctorAdviceRepositoryImpl
import com.example.SIMATS.navigation.SelectedPatientHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorAdviceViewModel(
    private val repository: DoctorAdviceRepository = DoctorAdviceRepositoryImpl(),
    patientId: String? = SelectedPatientHolder.selectedPatientId,
    patientName: String? = SelectedPatientHolder.selectedPatientName
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DoctorAdviceUiState(patientName = patientName ?: SelectedPatientHolder.selectedPatientName)
    )
    val uiState: StateFlow<DoctorAdviceUiState> = _uiState.asStateFlow()

    private var currentPatientId: String? = patientId ?: SelectedPatientHolder.selectedPatientId

    init {
        val id = currentPatientId
        if (!id.isNullOrBlank()) {
            loadData(id)
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "No patient selected"
            )
        }
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
            DoctorAdviceUiEvent.OnQuoteCardClicked -> {
                _uiState.value = _uiState.value.copy(isSymptomTimelineSheetOpen = true)
            }
            DoctorAdviceUiEvent.OnDismissSymptomTimeline -> {
                _uiState.value = _uiState.value.copy(isSymptomTimelineSheetOpen = false)
            }
            DoctorAdviceUiEvent.OnSendAdviceClicked -> {
                sendAdvice()
            }
            DoctorAdviceUiEvent.OnRetryClicked -> {
                val id = currentPatientId ?: SelectedPatientHolder.selectedPatientId
                if (!id.isNullOrBlank()) {
                    currentPatientId = id
                    loadData(id)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No patient selected"
                    )
                }
            }
            DoctorAdviceUiEvent.OnBackClicked -> {}
            DoctorAdviceUiEvent.OnNotificationClicked -> {}
        }
    }

    fun setPatientId(id: String, name: String? = null) {
        val resolvedName = name ?: SelectedPatientHolder.selectedPatientName
        if (id.isNotBlank() && (id != currentPatientId || _uiState.value.adviceData == null)) {
            currentPatientId = id
            SelectedPatientHolder.selectedPatientId = id
            if (!resolvedName.isNullOrBlank()) {
                SelectedPatientHolder.selectedPatientName = resolvedName
            }
            _uiState.value = _uiState.value.copy(patientName = resolvedName ?: _uiState.value.patientName)
            loadData(id)
        }
    }

    private fun loadData(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getAdviceData(id).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        if (data.patientName.isNotBlank()) {
                            SelectedPatientHolder.selectedPatientName = data.patientName
                        }
                        _uiState.value = _uiState.value.copy(
                            adviceData = data,
                            patientName = data.patientName.ifBlank { _uiState.value.patientName },
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
        val targetPatientId = currentPatientId ?: SelectedPatientHolder.selectedPatientId
        if (targetPatientId.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Cannot send advice: no patient selected")
            return
        }
        _uiState.value = _uiState.value.copy(isSending = true, errorMessage = null)
        viewModelScope.launch {
            repository.sendAdvice(
                _uiState.value.subjectText,
                _uiState.value.adviceText,
                _uiState.value.isSaveCopyChecked,
                targetPatientId
            ).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isSending = false, isSentSuccess = true, errorMessage = null)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isSending = false, errorMessage = err.message ?: "Failed to send advice")
                }
            }
        }
    }

    class Factory(
        private val repository: DoctorAdviceRepository = DoctorAdviceRepositoryImpl(),
        private val patientId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return DoctorAdviceViewModel(repository, patientId) as T
        }
    }
}
