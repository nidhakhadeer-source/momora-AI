package com.example.SIMATS.ui.adminprofile

import android.net.Uri

data class AdminProfileUiState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "Administrator",
    val photoUrl: String? = null,
    val localPhotoUri: Uri? = null,
    val isUploadingPhoto: Boolean = false,
    val isEditing: Boolean = false,
    val isSaving: Boolean = false,
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val showChangePasswordDialog: Boolean = false,
    val showLogoutConfirmDialog: Boolean = false,
    val currentPasswordInput: String = "",
    val newPasswordInput: String = "",
    val confirmPasswordInput: String = "",
    val passwordError: String? = null,
    val appTheme: String = "SYSTEM",
    val selectedNavIndex: Int = 0
)
