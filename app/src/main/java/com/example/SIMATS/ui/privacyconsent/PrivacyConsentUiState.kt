package com.example.SIMATS.ui.privacyconsent

data class PrivacyConsentUiState(
    val stepText: String = "GETTING TO KNOW YOU - 1 OF 5",
    val progress: Float = 0.2f,
    val isStoreHealthInfoEnabled: Boolean = true,
    val isPersonaliseAiEnabled: Boolean = true,
    val isProductAnalyticsEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
