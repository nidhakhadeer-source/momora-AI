package com.example.SIMATS.ui.privacyconsent

sealed interface PrivacyConsentUiEvent {
    data class OnToggleStoreHealthInfo(val enabled: Boolean) : PrivacyConsentUiEvent
    data class OnTogglePersonaliseAi(val enabled: Boolean) : PrivacyConsentUiEvent
    data class OnToggleProductAnalytics(val enabled: Boolean) : PrivacyConsentUiEvent
    data object OnReadPrivacyNoticeClicked : PrivacyConsentUiEvent
    data object OnSaveChoicesClicked : PrivacyConsentUiEvent
    data object OnSkipClicked : PrivacyConsentUiEvent
    data object OnBackClicked : PrivacyConsentUiEvent
    data object OnNavigationHandled : PrivacyConsentUiEvent
}
