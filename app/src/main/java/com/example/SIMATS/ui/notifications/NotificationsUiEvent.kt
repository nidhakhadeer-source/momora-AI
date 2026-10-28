package com.example.SIMATS.ui.notifications

sealed interface NotificationsUiEvent {
    data class OnFilterTabSelected(val index: Int) : NotificationsUiEvent
    data object OnMarkAllReadClicked : NotificationsUiEvent
    data class OnNotificationItemClicked(val id: String) : NotificationsUiEvent
    data class OnNavTabSelected(val index: Int) : NotificationsUiEvent
    data object OnPrivacySettingsClicked : NotificationsUiEvent
    data object OnBackClicked : NotificationsUiEvent
}
