package com.example.SIMATS.ui.adminprofile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.repository.AdminSettingsRepository
import com.example.SIMATS.data.repository.AdminSettingsRepositoryImpl
import com.example.SIMATS.ui.theme.ThemeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AdminProfileViewModel(
    private val repository: AdminSettingsRepository = AdminSettingsRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        AdminProfileUiState(
            fullName = SessionManager.getUserName(),
            email = SessionManager.getUserEmail(),
            phone = SessionManager.getPhone() ?: "",
            role = "Administrator",
            photoUrl = SessionManager.getPhotoUrl(),
            appTheme = ThemeManager.getTheme()
        )
    )
    val uiState: StateFlow<AdminProfileUiState> = _uiState.asStateFlow()

    init {
        loadAdminProfile()
        viewModelScope.launch {
            ThemeManager.themeFlow.collect { theme ->
                _uiState.value = _uiState.value.copy(appTheme = theme)
            }
        }
    }

    fun onEvent(event: AdminProfileUiEvent) {
        when (event) {
            is AdminProfileUiEvent.OnFullNameChanged -> {
                _uiState.value = _uiState.value.copy(fullName = event.value)
            }
            is AdminProfileUiEvent.OnPhoneChanged -> {
                _uiState.value = _uiState.value.copy(phone = event.value)
            }
            is AdminProfileUiEvent.OnPhotoSelected -> {
                saveAdminPhoto(event.uri, event.context)
            }
            AdminProfileUiEvent.OnToggleEditMode -> {
                _uiState.value = _uiState.value.copy(isEditing = !_uiState.value.isEditing)
            }
            AdminProfileUiEvent.OnCancelEditClicked -> {
                _uiState.value = _uiState.value.copy(
                    isEditing = false,
                    fullName = SessionManager.getUserName(),
                    phone = SessionManager.getPhone() ?: ""
                )
            }
            AdminProfileUiEvent.OnSaveClicked -> {
                saveProfile()
            }
            AdminProfileUiEvent.OnOpenChangePasswordDialog -> {
                _uiState.value = _uiState.value.copy(
                    showChangePasswordDialog = true,
                    currentPasswordInput = "",
                    newPasswordInput = "",
                    confirmPasswordInput = "",
                    passwordError = null
                )
            }
            AdminProfileUiEvent.OnDismissChangePasswordDialog -> {
                _uiState.value = _uiState.value.copy(showChangePasswordDialog = false)
            }
            is AdminProfileUiEvent.OnCurrentPasswordChanged -> {
                _uiState.value = _uiState.value.copy(currentPasswordInput = event.value, passwordError = null)
            }
            is AdminProfileUiEvent.OnNewPasswordChanged -> {
                _uiState.value = _uiState.value.copy(newPasswordInput = event.value, passwordError = null)
            }
            is AdminProfileUiEvent.OnConfirmPasswordChanged -> {
                _uiState.value = _uiState.value.copy(confirmPasswordInput = event.value, passwordError = null)
            }
            AdminProfileUiEvent.OnSubmitChangePassword -> {
                submitChangePassword()
            }
            AdminProfileUiEvent.OnOpenLogoutDialog -> {
                _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = true)
            }
            AdminProfileUiEvent.OnDismissLogoutDialog -> {
                _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = false)
            }
            AdminProfileUiEvent.OnLogoutConfirmed -> {
                _uiState.value = _uiState.value.copy(showLogoutConfirmDialog = false)
                logout()
            }
            is AdminProfileUiEvent.OnThemeSelected -> {
                ThemeManager.setTheme(event.theme)
                _uiState.value = _uiState.value.copy(appTheme = event.theme)
            }
            AdminProfileUiEvent.OnDismissMessages -> {
                _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
            }
            is AdminProfileUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
        }
    }

    private fun loadAdminProfile() {
        val sessionName = SessionManager.getUserName()
        val sessionEmail = SessionManager.getUserEmail()
        val sessionPhone = SessionManager.getPhone() ?: ""
        val sessionPhoto = SessionManager.getPhotoUrl()

        _uiState.value = _uiState.value.copy(
            fullName = sessionName.ifBlank { _uiState.value.fullName },
            email = sessionEmail.ifBlank { _uiState.value.email },
            phone = sessionPhone,
            photoUrl = sessionPhoto,
            isLoading = true
        )

        viewModelScope.launch {
            repository.getAdminSettings().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        val finalName = if (sessionName.isNotBlank()) sessionName else data.adminName
                        val rawEmail = data.adminEmailText.substringBefore("·").trim()
                        val finalEmail = if (sessionEmail.isNotBlank()) sessionEmail else rawEmail
                        _uiState.value = _uiState.value.copy(
                            fullName = finalName,
                            email = finalEmail,
                            isLoading = false
                        )
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(isLoading = false)
                    }
                )
            }
        }
    }

    private fun saveAdminPhoto(uri: Uri, context: Context) {
        try {
            val photoFile = File(context.filesDir, "admin_profile_photo.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                photoFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            if (photoFile.exists()) {
                val path = photoFile.absolutePath
                SessionManager.setPhotoUrl(path)
                _uiState.value = _uiState.value.copy(
                    photoUrl = path,
                    localPhotoUri = uri,
                    successMessage = "Profile photo updated successfully"
                )
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Failed to update profile photo: ${e.localizedMessage}"
            )
        }
    }

    private fun saveProfile() {
        val newName = _uiState.value.fullName.trim()
        val newPhone = _uiState.value.phone.trim()

        if (newName.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Name cannot be empty")
            return
        }

        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
        try {
            SessionManager.saveUserProfileDetails(phone = newPhone)
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                isEditing = false,
                fullName = newName,
                phone = newPhone,
                successMessage = "Admin profile updated successfully"
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isSaving = false,
                errorMessage = e.localizedMessage ?: "Failed to save profile"
            )
        }
    }

    private fun submitChangePassword() {
        val curr = _uiState.value.currentPasswordInput.trim()
        val newP = _uiState.value.newPasswordInput.trim()
        val confirmP = _uiState.value.confirmPasswordInput.trim()

        if (curr.isBlank()) {
            _uiState.value = _uiState.value.copy(passwordError = "Please enter your current password")
            return
        }
        if (newP.length < 6) {
            _uiState.value = _uiState.value.copy(passwordError = "New password must be at least 6 characters")
            return
        }
        if (newP != confirmP) {
            _uiState.value = _uiState.value.copy(passwordError = "Passwords do not match")
            return
        }

        _uiState.value = _uiState.value.copy(
            showChangePasswordDialog = false,
            currentPasswordInput = "",
            newPasswordInput = "",
            confirmPasswordInput = "",
            passwordError = null,
            successMessage = "Password updated successfully for current session"
        )
    }

    private fun logout() {
        _uiState.value = _uiState.value.copy(isLoggingOut = true)
        viewModelScope.launch {
            repository.logout().collect {
                SessionManager.clearSession()
                _uiState.value = _uiState.value.copy(isLoggingOut = false, isLoggedOut = true)
            }
        }
    }

    class Factory(private val repository: AdminSettingsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminProfileViewModel(repository) as T
        }
    }
}
