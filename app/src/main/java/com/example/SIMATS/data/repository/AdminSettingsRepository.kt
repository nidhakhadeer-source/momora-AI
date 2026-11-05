package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AdminSettingsData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface AdminSettingsRepository {
    fun getAdminSettings(): Flow<Result<AdminSettingsData>>
    fun logout(): Flow<Result<Boolean>>
}

class AdminSettingsRepositoryImpl : AdminSettingsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAdminSettings(): Flow<Result<AdminSettingsData>> = flow {
        try {
            val response = apiService.getAdminSettings()
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load admin settings")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network connection error")))
            }
        }
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
