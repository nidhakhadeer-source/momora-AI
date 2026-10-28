package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.AdminSettingsData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AdminSettingsRepository {
    fun getAdminSettings(): Flow<AdminSettingsData>
    fun logout(): Flow<Result<Boolean>>
}

class AdminSettingsRepositoryImpl : AdminSettingsRepository {
    override fun getAdminSettings(): Flow<AdminSettingsData> = flow {
        emit(
            AdminSettingsData(
                adminName = "Alex Morgan",
                adminEmailText = "admin@willow.example · Administrator",
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
        delay(500)
        emit(Result.success(true))
    }
}
