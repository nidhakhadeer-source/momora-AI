package com.example.SIMATS.ui.privacyconsent

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PrivacyConsentViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacyConsentUiState())
    val uiState: StateFlow<PrivacyConsentUiState> = _uiState.asStateFlow()

    fun onEvent(event: PrivacyConsentUiEvent) {
        when (event) {
            is PrivacyConsentUiEvent.OnToggleStoreHealthInfo -> {
                _uiState.update { it.copy(isStoreHealthInfoEnabled = event.enabled) }
            }
            is PrivacyConsentUiEvent.OnTogglePersonaliseAi -> {
                _uiState.update { it.copy(isPersonaliseAiEnabled = event.enabled) }
            }
            is PrivacyConsentUiEvent.OnToggleProductAnalytics -> {
                _uiState.update { it.copy(isProductAnalyticsEnabled = event.enabled) }
            }
            PrivacyConsentUiEvent.OnReadPrivacyNoticeClicked -> {
                // Action for privacy notice
            }
            PrivacyConsentUiEvent.OnSaveChoicesClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ProfileSetup.route) }
            }
            PrivacyConsentUiEvent.OnSkipClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.ProfileSetup.route) }
            }
            PrivacyConsentUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            PrivacyConsentUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
