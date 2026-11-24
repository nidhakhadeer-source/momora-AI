package com.example.SIMATS.data.model

import com.google.gson.annotations.SerializedName

data class AdminSessionTimeoutData(
    @SerializedName("session_timeout_minutes")
    val sessionTimeoutMinutes: Int = 15
)

data class AdminSessionItem(
    val id: Int,
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("expires_at")
    val expiresAt: String = "",
    @SerializedName("device_info")
    val deviceInfo: String? = null,
    @SerializedName("is_current")
    val isCurrent: Boolean = false
)

data class AdminProfileData(
    val name: String = "",
    val email: String = "",
    val role: String = "admin",
    @SerializedName("created_at")
    val createdAt: String = "",
    @SerializedName("is_active")
    val isActive: Int = 1
)

data class AdminAuditEvent(
    val description: String = "",
    val time: String = "",
    val type: String? = null
)
