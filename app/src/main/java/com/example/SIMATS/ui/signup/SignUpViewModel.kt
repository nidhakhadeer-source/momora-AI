package com.example.SIMATS.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.PatientRegistrationData
import com.example.SIMATS.data.repository.SignUpRepository
import com.example.SIMATS.data.repository.SignUpRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val repository: SignUpRepository = SignUpRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onEvent(event: SignUpUiEvent) {
        when (event) {
            is SignUpUiEvent.OnFullNameChanged -> {
                _uiState.value = _uiState.value.copy(fullName = event.fullName, errorMessage = null)
            }
            is SignUpUiEvent.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(email = event.email, errorMessage = null)
            }
            is SignUpUiEvent.OnDateOfBirthChanged -> {
                _uiState.value = _uiState.value.copy(dateOfBirth = event.dob, errorMessage = null)
            }
            is SignUpUiEvent.OnPasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.password, errorMessage = null)
            }
            is SignUpUiEvent.OnConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(confirmPassword = event.password, errorMessage = null)
            }
            SignUpUiEvent.OnTogglePasswordVisibility -> {
                _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
            }
            SignUpUiEvent.OnToggleConfirmPasswordVisibility -> {
                _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
            }
            is SignUpUiEvent.OnTermsAgreedToggled -> {
                _uiState.value = _uiState.value.copy(isTermsAgreed = event.agreed)
            }
            SignUpUiEvent.OnRegisterClicked -> {
                performRegistration()
            }
            SignUpUiEvent.OnLoginClicked -> {}
            SignUpUiEvent.OnBackClicked -> {}
            SignUpUiEvent.OnHelpClicked -> {}
        }
    }

    private fun performRegistration() {
        val state = _uiState.value
        if (!state.isTermsAgreed) {
            _uiState.value = state.copy(errorMessage = "Please agree to the Terms and Privacy Notice.")
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val regData = PatientRegistrationData(
                fullName = state.fullName,
                email = state.email,
                dateOfBirth = state.dateOfBirth,
                password = state.password,
                isTermsAgreed = state.isTermsAgreed
            )
            repository.registerPatient(regData).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isRegistrationSuccess = true)
                }.onFailure { error ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                }
            }
        }
    }

    class Factory(private val repository: SignUpRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SignUpViewModel(repository) as T
        }
    }
}
