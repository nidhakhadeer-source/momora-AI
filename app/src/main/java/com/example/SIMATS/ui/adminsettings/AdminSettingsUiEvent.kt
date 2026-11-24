package com.example.SIMATS.ui.adminsettings

sealed interface AdminSettingsUiEvent {
    data class OnNavTabSelected(val index: Int) : AdminSettingsUiEvent
    data object OnChangePasswordClicked : AdminSettingsUiEvent
    data object OnMfaClicked : AdminSettingsUiEvent
    data object OnSessionTimeoutClicked : AdminSettingsUiEvent
    data object OnActiveSessionsClicked : AdminSettingsUiEvent
    data object OnAccountAccessAuditClicked : AdminSettingsUiEvent
    data object OnAdminProfileClicked : AdminSettingsUiEvent
    data object OnDismissSheet : AdminSettingsUiEvent
    data object OnLogoutClicked : AdminSettingsUiEvent
    data object OnBackClicked : AdminSettingsUiEvent
    data object OnNotificationClicked : AdminSettingsUiEvent
    data object OnRetryClicked : AdminSettingsUiEvent

    // Change Password Events
    data class OnCurrentPasswordChanged(val value: String) : AdminSettingsUiEvent
    data class OnNewPasswordChanged(val value: String) : AdminSettingsUiEvent
    data class OnConfirmPasswordChanged(val value: String) : AdminSettingsUiEvent
    data object OnToggleCurrentPasswordVisibility : AdminSettingsUiEvent
    data object OnToggleNewPasswordVisibility : AdminSettingsUiEvent
    data object OnToggleConfirmPasswordVisibility : AdminSettingsUiEvent
    data object OnSubmitChangePassword : AdminSettingsUiEvent

    // Session Timeout Events
    data class OnSelectTimeoutMinutes(val minutes: Int) : AdminSettingsUiEvent
    data object OnSubmitSessionTimeout : AdminSettingsUiEvent

    // Active Sessions Events
    data class OnRevokeSession(val sessionId: Int) : AdminSettingsUiEvent
    data object OnRefreshSessions : AdminSettingsUiEvent

    // Profile & Audit refresh events
    data object OnRefreshProfile : AdminSettingsUiEvent
    data object OnRefreshAudit : AdminSettingsUiEvent
}
