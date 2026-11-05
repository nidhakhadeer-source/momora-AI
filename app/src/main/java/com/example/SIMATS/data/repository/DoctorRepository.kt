package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorDashboardData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface DoctorRepository {
    fun getDoctorDashboardData(): Flow<Result<DoctorDashboardData>>
}

class DoctorRepositoryImpl : DoctorRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDoctorDashboardData(): Flow<Result<DoctorDashboardData>> = flow {
        try {
            val response = apiService.getDoctorDashboard(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load doctor dashboard")))
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
