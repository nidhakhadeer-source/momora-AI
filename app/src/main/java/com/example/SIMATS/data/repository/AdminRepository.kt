package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AdminDashboardData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface AdminRepository {
    fun getAdminDashboardData(): Flow<Result<AdminDashboardData>>
}

class AdminRepositoryImpl : AdminRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAdminDashboardData(): Flow<Result<AdminDashboardData>> = flow {
        val userId = SessionManager.getUserId()
        try {
            val response = apiService.getAdminDashboard(if (userId > 0) userId else null)
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load admin dashboard")))
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
}
