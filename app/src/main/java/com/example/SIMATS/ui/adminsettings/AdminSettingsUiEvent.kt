package com.example.SIMATS.ui.adminsettings

sealed interface AdminSettingsUiEvent {
    data class OnNavTabSelected(val index: Int) : AdminSettingsUiEvent
    data object OnChangePasswordClicked : AdminSettingsUiEvent
    data object OnMfaClicked : AdminSettingsUiEvent
    data object OnSessionTimeoutClicked : AdminSettingsUiEvent
    data object OnActiveSessionsClicked : AdminSettingsUiEvent
    data object OnAccountAccessAuditClicked : AdminSettingsUiEvent
    data object OnLogoutClicked : AdminSettingsUiEvent
    data object OnBackClicked : AdminSettingsUiEvent
    data object OnNotificationClicked : AdminSettingsUiEvent
}
