package com.example.SIMATS.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.UserRole
import com.example.SIMATS.data.repository.AuthRepository
import com.example.SIMATS.data.repository.AuthRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEvent(event: LoginUiEvent) {
        when (event) {
            is LoginUiEvent.OnRoleSelected -> {
                _uiState.value = _uiState.value.copy(selectedRole = event.role)
            }
            is LoginUiEvent.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(email = event.email, errorMessage = null)
            }
            is LoginUiEvent.OnPasswordChanged -> {
                _uiState.value = _uiState.value.copy(password = event.password, errorMessage = null)
            }
            LoginUiEvent.OnTogglePasswordVisibility -> {
                _uiState.value = _uiState.value.copy(isPasswordVisible = !_uiState.value.isPasswordVisible)
            }
            LoginUiEvent.OnLoginClicked -> {
                performLogin()
            }
            LoginUiEvent.OnForgotPasswordClicked -> {
                // Navigate or handle forgot password
            }
            LoginUiEvent.OnCreateAccountClicked -> {
                // Navigate to signup
            }
            LoginUiEvent.OnBackClicked -> {
                // Navigate back
            }
            LoginUiEvent.OnHelpClicked -> {
                // Help action
            }
        }
    }

    private fun performLogin() {
        val currentState = _uiState.value
        _uiState.value = currentState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            authRepository.login(currentState.email, currentState.password, currentState.selectedRole)
                .collect { result ->
                    result.onSuccess {
                        _uiState.value = _uiState.value.copy(isLoading = false, isLoginSuccess = true)
                    }.onFailure { error ->
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = error.message)
                    }
                }
        }
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return LoginViewModel(authRepository) as T
        }
    }
}
