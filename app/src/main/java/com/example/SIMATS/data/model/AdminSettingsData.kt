package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class AdminSettingsData(
    val adminName: String,
    val adminEmailText: String,
    val passwordLastUpdatedText: String,
    val isMfaEnabled: Boolean,
    val mfaStatusText: String,
    val sessionTimeoutText: String,
    @SerializedName("session_timeout_minutes", alternate = ["sessionTimeoutMinutes"])
    val sessionTimeoutMinutes: Int = 15,
    val activeSessionsText: String,
    val appVersionText: String
)

