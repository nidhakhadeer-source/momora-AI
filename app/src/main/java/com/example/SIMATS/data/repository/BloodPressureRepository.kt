package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.BloodPressureData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface BloodPressureRepository {
    fun getBloodPressureData(): Flow<Result<BloodPressureData>>
    fun saveBloodPressureReading(systolic: String, diastolic: String): Flow<Result<Boolean>>
}

class BloodPressureRepositoryImpl : BloodPressureRepository {
    private val apiService = RetrofitClient.apiService

    override fun getBloodPressureData(): Flow<Result<BloodPressureData>> = flow {
        try {
            val response = apiService.getBloodPressureData(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load blood pressure data")))
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

    override fun saveBloodPressureReading(systolic: String, diastolic: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.saveBloodPressure(systolic, diastolic)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to log blood pressure")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error while logging blood pressure")))
            }
        }
    }
}
