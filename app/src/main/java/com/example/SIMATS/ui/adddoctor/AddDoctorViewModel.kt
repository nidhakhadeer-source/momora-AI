package com.example.SIMATS.ui.adddoctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.AddDoctorRepository
import com.example.SIMATS.data.repository.AddDoctorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddDoctorViewModel(
    private val repository: AddDoctorRepository = AddDoctorRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDoctorUiState())
    val uiState: StateFlow<AddDoctorUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: AddDoctorUiEvent) {
        when (event) {
            is AddDoctorUiEvent.OnNameChanged -> {
                _uiState.value = _uiState.value.copy(nameInput = event.name)
            }
            is AddDoctorUiEvent.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(emailInput = event.email)
            }
            is AddDoctorUiEvent.OnSpecialtyChanged -> {
                _uiState.value = _uiState.value.copy(specialtyInput = event.specialty)
            }
            is AddDoctorUiEvent.OnLicenseIdChanged -> {
                _uiState.value = _uiState.value.copy(licenseIdInput = event.licenseId)
            }
            is AddDoctorUiEvent.OnClinicChanged -> {
                _uiState.value = _uiState.value.copy(clinicInput = event.clinic)
            }
            is AddDoctorUiEvent.OnSendActivationToggled -> {
                _uiState.value = _uiState.value.copy(isSendActivationChecked = event.isChecked)
            }
            is AddDoctorUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            AddDoctorUiEvent.OnCreateAccountClicked -> {
                createDoctorAccount()
            }
            AddDoctorUiEvent.OnBackClicked -> {}
            AddDoctorUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            repository.getDefaultDoctorFormData().collect { data ->
                _uiState.value = _uiState.value.copy(
                    formData = data,
                    nameInput = data.defaultName,
                    emailInput = data.defaultEmail,
                    specialtyInput = data.defaultSpecialty,
                    licenseIdInput = data.defaultLicenseId,
                    clinicInput = data.defaultClinic
                )
            }
        }
    }

    private fun createDoctorAccount() {
        _uiState.value = _uiState.value.copy(isCreating = true, errorMessage = null)
        viewModelScope.launch {
            repository.createDoctorAccount(
                _uiState.value.nameInput,
                _uiState.value.emailInput,
                _uiState.value.specialtyInput,
                _uiState.value.licenseIdInput,
                _uiState.value.clinicInput,
                _uiState.value.isSendActivationChecked
            ).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isCreating = false, isCreatedSuccess = true)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isCreating = false, errorMessage = err.message)
                }
            }
        }
    }

    class Factory(private val repository: AddDoctorRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddDoctorViewModel(repository) as T
        }
    }
}
