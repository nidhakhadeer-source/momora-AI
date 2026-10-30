package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AdminSettingsData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AdminSettingsRepository {
    fun getAdminSettings(): Flow<AdminSettingsData>
    fun logout(): Flow<Result<Boolean>>
}

class AdminSettingsRepositoryImpl : AdminSettingsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAdminSettings(): Flow<AdminSettingsData> = flow {
        try {
            val response = apiService.getAdminSettings()
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            AdminSettingsData(
                adminName = SessionManager.getUserName().ifBlank { "Alex Morgan" },
                adminEmailText = "${SessionManager.getUserEmail().ifBlank { "admin@willow.example" }} · Administrator",
                passwordLastUpdatedText = "Last updated 01 Oct 2026",
                isMfaEnabled = true,
                mfaStatusText = "Authenticator app connected",
                sessionTimeoutText = "Automatically sign out after 15 minutes",
                activeSessionsText = "1 device · This Android device",
                appVersionText = "Momora AI · v1.0.0 · Ends your session on this device."
            )
        )
    }

    override fun logout(): Flow<Result<Boolean>> = flow {
        try {
            apiService.adminLogout()
            SessionManager.clearSession()
            emit(Result.success(true))
        } catch (_: Exception) {
            SessionManager.clearSession()
            emit(Result.success(true))
        }
    }
}
