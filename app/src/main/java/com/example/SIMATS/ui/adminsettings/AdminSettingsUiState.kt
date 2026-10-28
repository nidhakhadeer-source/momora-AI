package com.example.SIMATS.ui.adminsettings

import com.example.SIMATS.data.model.AdminSettingsData

data class AdminSettingsUiState(
    val settingsData: AdminSettingsData? = null,
    val selectedNavIndex: Int = 3, // Settings tab active (index 3)
    val isLoggingOut: Boolean = false,
    val isLoggedOutSuccess: Boolean = false,
    val errorMessage: String? = null
)
