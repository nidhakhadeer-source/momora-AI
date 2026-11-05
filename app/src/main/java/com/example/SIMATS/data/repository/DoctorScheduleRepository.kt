package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorScheduleData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface DoctorScheduleRepository {
    fun getScheduleData(): Flow<Result<DoctorScheduleData>>
}

class DoctorScheduleRepositoryImpl : DoctorScheduleRepository {
    private val apiService = RetrofitClient.apiService

    override fun getScheduleData(): Flow<Result<DoctorScheduleData>> = flow {
        try {
            val response = apiService.getDoctorSchedule(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load doctor schedule")))
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
