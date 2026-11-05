package com.example.SIMATS.ui.profile

import android.content.Context
import android.net.Uri

sealed interface ProfileSetupUiEvent {
    data class OnFullNameChanged(val fullName: String) : ProfileSetupUiEvent
    data class OnEmailChanged(val email: String) : ProfileSetupUiEvent
    data class OnPhoneChanged(val phone: String) : ProfileSetupUiEvent
    data class OnDateOfBirthChanged(val dob: String) : ProfileSetupUiEvent
    data class OnGenderChanged(val gender: String) : ProfileSetupUiEvent
    data class OnBloodGroupChanged(val bloodGroup: String) : ProfileSetupUiEvent
    data class OnAddressChanged(val address: String) : ProfileSetupUiEvent
    data class OnPhotoSelected(val uri: Uri, val context: Context) : ProfileSetupUiEvent
    data class OnCareTeamSharingToggled(val enabled: Boolean) : ProfileSetupUiEvent
    data class OnNavTabSelected(val index: Int) : ProfileSetupUiEvent
    data object OnSaveClicked : ProfileSetupUiEvent
    data object OnBackClicked : ProfileSetupUiEvent
    data object OnNotificationClicked : ProfileSetupUiEvent
    data object OnReminderPreferencesClicked : ProfileSetupUiEvent
    data object OnExportHealthInfoClicked : ProfileSetupUiEvent
    data object OnLogoutClicked : ProfileSetupUiEvent
    data object OnRetryClicked : ProfileSetupUiEvent
    data object DismissMessages : ProfileSetupUiEvent
}
