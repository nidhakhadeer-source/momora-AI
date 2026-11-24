package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AdminAuditEvent
import com.example.SIMATS.data.model.AdminProfileData
import com.example.SIMATS.data.model.AdminSessionItem
import com.example.SIMATS.data.model.AdminSettingsData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface AdminSettingsRepository {
    fun getAdminSettings(): Flow<Result<AdminSettingsData>>
    fun logout(): Flow<Result<Boolean>>
    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String): Flow<Result<String>>
    fun getSessionTimeout(): Flow<Result<Int>>
    fun setSessionTimeout(minutes: Int): Flow<Result<Int>>
    fun getSessions(): Flow<Result<List<AdminSessionItem>>>
    fun revokeSession(sessionId: Int): Flow<Result<Boolean>>
    fun getAdminProfile(): Flow<Result<AdminProfileData>>
    fun getAuditTrail(): Flow<Result<List<AdminAuditEvent>>>
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

    override fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Result<String>> = flow {
        try {
            val response = apiService.adminChangePassword(currentPassword, newPassword, confirmPassword)
            if (response.success) {
                emit(Result.success(response.message ?: "Password updated successfully"))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to update password")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Failed to update password")))
        }
    }

    override fun getSessionTimeout(): Flow<Result<Int>> = flow {
        try {
            val response = apiService.getAdminSessionTimeout()
            if (response.success && response.data != null) {
                emit(Result.success(response.data.sessionTimeoutMinutes))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load session timeout")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Failed to load session timeout")))
        }
    }

    override fun setSessionTimeout(minutes: Int): Flow<Result<Int>> = flow {
        try {
            val response = apiService.setAdminSessionTimeout(minutes)
            if (response.success) {
                emit(Result.success(minutes))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to set session timeout")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Failed to set session timeout")))
        }
    }

    override fun getSessions(): Flow<Result<List<AdminSessionItem>>> = flow {
        try {
            val response = apiService.getAdminSessions()
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load active sessions")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Failed to load active sessions")))
        }
    }

    override fun revokeSession(sessionId: Int): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.revokeAdminSession(action = "revoke", sessionId = sessionId)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to revoke session")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Failed to revoke session")))
        }
    }

    override fun getAdminProfile(): Flow<Result<AdminProfileData>> = flow {
        try {
            val response = apiService.getAdminProfile()
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load admin profile")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Failed to load admin profile")))
        }
    }

    override fun getAuditTrail(): Flow<Result<List<AdminAuditEvent>>> = flow {
        try {
            val response = apiService.getAdminAuditTrail()
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load audit trail")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Failed to load audit trail")))
        }
    }
}
