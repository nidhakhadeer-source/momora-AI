package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.HealthMonitoringData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface HealthMonitoringRepository {
    fun getHealthMonitoringData(): Flow<Result<HealthMonitoringData>>
}

class HealthMonitoringRepositoryImpl : HealthMonitoringRepository {
    private val apiService = RetrofitClient.apiService

    override fun getHealthMonitoringData(): Flow<Result<HealthMonitoringData>> = flow {
        try {
            val response = apiService.getHealthVitals(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load health vitals")))
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
