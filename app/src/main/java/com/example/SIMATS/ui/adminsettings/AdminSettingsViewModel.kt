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
            AdminSettingsUiEvent.OnChangePasswordClicked -> {
                _uiState.value = _uiState.value.copy(
                    activeSheet = AdminSettingsSheet.CHANGE_PASSWORD,
                    currentPasswordInput = "",
                    newPasswordInput = "",
                    confirmPasswordInput = "",
                    changePasswordError = null,
                    changePasswordSuccess = null
                )
            }
            AdminSettingsUiEvent.OnMfaClicked -> {
                // Not clickable / not set up
            }
            AdminSettingsUiEvent.OnSessionTimeoutClicked -> {
                val currentTimeout = _uiState.value.settingsData?.sessionTimeoutMinutes ?: 15
                _uiState.value = _uiState.value.copy(
                    activeSheet = AdminSettingsSheet.SESSION_TIMEOUT,
                    selectedTimeoutMinutes = currentTimeout,
                    timeoutError = null,
                    timeoutSuccess = null
                )
                loadSessionTimeout()
            }
            AdminSettingsUiEvent.OnActiveSessionsClicked -> {
                _uiState.value = _uiState.value.copy(
                    activeSheet = AdminSettingsSheet.ACTIVE_SESSIONS,
                    sessionsError = null,
                    sessionsSuccess = null
                )
                loadSessions()
            }
            AdminSettingsUiEvent.OnAdminProfileClicked -> {
                _uiState.value = _uiState.value.copy(
                    activeSheet = AdminSettingsSheet.ADMIN_PROFILE,
                    profileError = null
                )
                loadProfile()
            }
            AdminSettingsUiEvent.OnAccountAccessAuditClicked -> {
                _uiState.value = _uiState.value.copy(
                    activeSheet = AdminSettingsSheet.AUDIT_TRAIL,
                    auditError = null
                )
                loadAuditTrail()
            }
            AdminSettingsUiEvent.OnDismissSheet -> {
                _uiState.value = _uiState.value.copy(activeSheet = AdminSettingsSheet.NONE)
            }
            AdminSettingsUiEvent.OnLogoutClicked -> {
                logout()
            }
            AdminSettingsUiEvent.OnBackClicked -> {}
            AdminSettingsUiEvent.OnNotificationClicked -> {}
            AdminSettingsUiEvent.OnRetryClicked -> {
                loadSettings()
            }

            // Change Password
            is AdminSettingsUiEvent.OnCurrentPasswordChanged -> {
                _uiState.value = _uiState.value.copy(currentPasswordInput = event.value, changePasswordError = null)
            }
            is AdminSettingsUiEvent.OnNewPasswordChanged -> {
                _uiState.value = _uiState.value.copy(newPasswordInput = event.value, changePasswordError = null)
            }
            is AdminSettingsUiEvent.OnConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(confirmPasswordInput = event.value, changePasswordError = null)
            }
            AdminSettingsUiEvent.OnToggleCurrentPasswordVisibility -> {
                _uiState.value = _uiState.value.copy(isCurrentPasswordVisible = !_uiState.value.isCurrentPasswordVisible)
            }
            AdminSettingsUiEvent.OnToggleNewPasswordVisibility -> {
                _uiState.value = _uiState.value.copy(isNewPasswordVisible = !_uiState.value.isNewPasswordVisible)
            }
            AdminSettingsUiEvent.OnToggleConfirmPasswordVisibility -> {
                _uiState.value = _uiState.value.copy(isConfirmPasswordVisible = !_uiState.value.isConfirmPasswordVisible)
            }
            AdminSettingsUiEvent.OnSubmitChangePassword -> {
                submitChangePassword()
            }

            // Session Timeout
            is AdminSettingsUiEvent.OnSelectTimeoutMinutes -> {
                _uiState.value = _uiState.value.copy(selectedTimeoutMinutes = event.minutes, timeoutError = null)
            }
            AdminSettingsUiEvent.OnSubmitSessionTimeout -> {
                submitSessionTimeout()
            }

            // Sessions
            is AdminSettingsUiEvent.OnRevokeSession -> {
                revokeSession(event.sessionId)
            }
            AdminSettingsUiEvent.OnRefreshSessions -> {
                loadSessions()
            }

            // Profile & Audit refresh
            AdminSettingsUiEvent.OnRefreshProfile -> {
                loadProfile()
            }
            AdminSettingsUiEvent.OnRefreshAudit -> {
                loadAuditTrail()
            }
        }
    }

    fun loadSettings() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getAdminSettings().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            settingsData = data,
                            selectedTimeoutMinutes = data.sessionTimeoutMinutes
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load admin settings"
                        )
                    }
                )
            }
        }
    }

    private fun submitChangePassword() {
        val curr = _uiState.value.currentPasswordInput.trim()
        val newP = _uiState.value.newPasswordInput.trim()
        val conf = _uiState.value.confirmPasswordInput.trim()

        if (curr.isBlank() || newP.isBlank() || conf.isBlank()) {
            _uiState.value = _uiState.value.copy(changePasswordError = "All password fields are required.")
            return
        }
        if (newP.length < 8) {
            _uiState.value = _uiState.value.copy(changePasswordError = "New password must be at least 8 characters long.")
            return
        }
        if (newP != conf) {
            _uiState.value = _uiState.value.copy(changePasswordError = "New password and confirmation do not match.")
            return
        }

        _uiState.value = _uiState.value.copy(isChangingPassword = true, changePasswordError = null, changePasswordSuccess = null)
        viewModelScope.launch {
            repository.changePassword(curr, newP, conf).collect { result ->
                result.fold(
                    onSuccess = { msg ->
                        _uiState.value = _uiState.value.copy(
                            isChangingPassword = false,
                            changePasswordSuccess = msg,
                            currentPasswordInput = "",
                            newPasswordInput = "",
                            confirmPasswordInput = ""
                        )
                        loadSettings()
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isChangingPassword = false,
                            changePasswordError = err.localizedMessage ?: "Failed to update password"
                        )
                    }
                )
            }
        }
    }

    private fun loadSessionTimeout() {
        viewModelScope.launch {
            repository.getSessionTimeout().collect { result ->
                result.onSuccess { mins ->
                    _uiState.value = _uiState.value.copy(selectedTimeoutMinutes = mins)
                }
            }
        }
    }

    private fun submitSessionTimeout() {
        val minutes = _uiState.value.selectedTimeoutMinutes
        _uiState.value = _uiState.value.copy(isUpdatingTimeout = true, timeoutError = null, timeoutSuccess = null)
        viewModelScope.launch {
            repository.setSessionTimeout(minutes).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isUpdatingTimeout = false,
                            timeoutSuccess = "Session timeout saved to $minutes minutes."
                        )
                        loadSettings()
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isUpdatingTimeout = false,
                            timeoutError = err.localizedMessage ?: "Failed to update session timeout"
                        )
                    }
                )
            }
        }
    }

    private fun loadSessions() {
        _uiState.value = _uiState.value.copy(isLoadingSessions = true, sessionsError = null)
        viewModelScope.launch {
            repository.getSessions().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingSessions = false,
                            sessionsList = list
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingSessions = false,
                            sessionsError = err.localizedMessage ?: "Failed to load active sessions"
                        )
                    }
                )
            }
        }
    }

    private fun revokeSession(sessionId: Int) {
        _uiState.value = _uiState.value.copy(revokingSessionId = sessionId, sessionsError = null, sessionsSuccess = null)
        viewModelScope.launch {
            repository.revokeSession(sessionId).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            revokingSessionId = null,
                            sessionsSuccess = "Session revoked successfully."
                        )
                        loadSessions()
                        loadSettings()
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            revokingSessionId = null,
                            sessionsError = err.localizedMessage ?: "Failed to revoke session"
                        )
                    }
                )
            }
        }
    }

    private fun loadProfile() {
        _uiState.value = _uiState.value.copy(isLoadingProfile = true, profileError = null)
        viewModelScope.launch {
            repository.getAdminProfile().collect { result ->
                result.fold(
                    onSuccess = { prof ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingProfile = false,
                            profileData = prof
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingProfile = false,
                            profileError = err.localizedMessage ?: "Failed to load admin profile"
                        )
                    }
                )
            }
        }
    }

    private fun loadAuditTrail() {
        _uiState.value = _uiState.value.copy(isLoadingAudit = true, auditError = null)
        viewModelScope.launch {
            repository.getAuditTrail().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingAudit = false,
                            auditList = list
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoadingAudit = false,
                            auditError = err.localizedMessage ?: "Failed to load audit trail"
                        )
                    }
                )
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
