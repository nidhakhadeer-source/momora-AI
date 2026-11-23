package com.example.SIMATS.ui.adminprofile

import android.content.Context
import android.net.Uri

sealed interface AdminProfileUiEvent {
    data class OnFullNameChanged(val value: String) : AdminProfileUiEvent
    data class OnPhoneChanged(val value: String) : AdminProfileUiEvent
    data class OnPhotoSelected(val uri: Uri, val context: Context) : AdminProfileUiEvent
    data object OnToggleEditMode : AdminProfileUiEvent
    data object OnCancelEditClicked : AdminProfileUiEvent
    data object OnSaveClicked : AdminProfileUiEvent
    data object OnOpenChangePasswordDialog : AdminProfileUiEvent
    data object OnDismissChangePasswordDialog : AdminProfileUiEvent
    data class OnCurrentPasswordChanged(val value: String) : AdminProfileUiEvent
    data class OnNewPasswordChanged(val value: String) : AdminProfileUiEvent
    data class OnConfirmPasswordChanged(val value: String) : AdminProfileUiEvent
    data object OnSubmitChangePassword : AdminProfileUiEvent
    data object OnOpenLogoutDialog : AdminProfileUiEvent
    data object OnDismissLogoutDialog : AdminProfileUiEvent
    data object OnLogoutConfirmed : AdminProfileUiEvent
    data class OnThemeSelected(val theme: String) : AdminProfileUiEvent
    data object OnDismissMessages : AdminProfileUiEvent
    data class OnNavTabSelected(val index: Int) : AdminProfileUiEvent
}
