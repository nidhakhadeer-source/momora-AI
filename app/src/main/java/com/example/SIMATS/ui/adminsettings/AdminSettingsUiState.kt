package com.example.SIMATS.ui.adminsettings

import com.example.SIMATS.data.model.AdminAuditEvent
import com.example.SIMATS.data.model.AdminProfileData
import com.example.SIMATS.data.model.AdminSessionItem
import com.example.SIMATS.data.model.AdminSettingsData

enum class AdminSettingsSheet {
    NONE,
    CHANGE_PASSWORD,
    SESSION_TIMEOUT,
    ACTIVE_SESSIONS,
    ADMIN_PROFILE,
    AUDIT_TRAIL
}

data class AdminSettingsUiState(
    val settingsData: AdminSettingsData? = null,
    val selectedNavIndex: Int = 3, // Settings tab active (index 3)
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isLoggedOutSuccess: Boolean = false,
    val errorMessage: String? = null,

    // Sheet visibility
    val activeSheet: AdminSettingsSheet = AdminSettingsSheet.NONE,

    // Change Password
    val currentPasswordInput: String = "",
    val newPasswordInput: String = "",
    val confirmPasswordInput: String = "",
    val isCurrentPasswordVisible: Boolean = false,
    val isNewPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isChangingPassword: Boolean = false,
    val changePasswordError: String? = null,
    val changePasswordSuccess: String? = null,

    // Session Timeout
    val selectedTimeoutMinutes: Int = 15,
    val isUpdatingTimeout: Boolean = false,
    val timeoutError: String? = null,
    val timeoutSuccess: String? = null,

    // Active Sessions
    val sessionsList: List<AdminSessionItem> = emptyList(),
    val isLoadingSessions: Boolean = false,
    val revokingSessionId: Int? = null,
    val sessionsError: String? = null,
    val sessionsSuccess: String? = null,

    // Admin Profile
    val profileData: AdminProfileData? = null,
    val isLoadingProfile: Boolean = false,
    val profileError: String? = null,

    // Audit Trail
    val auditList: List<AdminAuditEvent> = emptyList(),
    val isLoadingAudit: Boolean = false,
    val auditError: String? = null
)
