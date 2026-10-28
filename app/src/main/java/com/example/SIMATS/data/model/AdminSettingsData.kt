package com.example.SIMATS.data.model

data class AdminSettingsData(
    val adminName: String,
    val adminEmailText: String,
    val passwordLastUpdatedText: String,
    val isMfaEnabled: Boolean,
    val mfaStatusText: String,
    val sessionTimeoutText: String,
    val activeSessionsText: String,
    val appVersionText: String
)
