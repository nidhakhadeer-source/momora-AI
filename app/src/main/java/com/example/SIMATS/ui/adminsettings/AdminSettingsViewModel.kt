package com.example.SIMATS.ui.adminsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.AdminSettingsRepository
import com.example.SIMATS.data.repository.AdminSettingsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminSettingsViewModel(
    private val repository: AdminSettingsRepository = AdminSettingsRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminSettingsUiState())
    val uiState: StateFlow<AdminSettingsUiState> = _uiState.asStateFlow()

    init {
        loadSettings()
    }

    fun onEvent(event: AdminSettingsUiEvent) {
        when (event) {
            is AdminSettingsUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            AdminSettingsUiEvent.OnChangePasswordClicked -> {}
            AdminSettingsUiEvent.OnMfaClicked -> {}
            AdminSettingsUiEvent.OnSessionTimeoutClicked -> {}
            AdminSettingsUiEvent.OnActiveSessionsClicked -> {}
            AdminSettingsUiEvent.OnAccountAccessAuditClicked -> {}
            AdminSettingsUiEvent.OnLogoutClicked -> {
                logout()
            }
            AdminSettingsUiEvent.OnBackClicked -> {}
            AdminSettingsUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            repository.getAdminSettings().collect { data ->
                _uiState.value = _uiState.value.copy(settingsData = data)
            }
        }
    }

    private fun logout() {
        _uiState.value = _uiState.value.copy(isLoggingOut = true)
        viewModelScope.launch {
            repository.logout().collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoggingOut = false, isLoggedOutSuccess = true)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isLoggingOut = false, errorMessage = err.message)
                }
            }
        }
    }

    class Factory(private val repository: AdminSettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminSettingsViewModel(repository) as T
        }
    }
}
