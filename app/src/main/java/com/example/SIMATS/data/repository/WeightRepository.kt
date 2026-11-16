package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.WeightTrackerData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface WeightRepository {
    fun getWeightTrackerData(): Flow<Result<WeightTrackerData>>
    fun saveWeightReading(weight: String, date: String): Flow<Result<Boolean>>
}

class WeightRepositoryImpl : WeightRepository {
    private val apiService = RetrofitClient.apiService

    override fun getWeightTrackerData(): Flow<Result<WeightTrackerData>> = flow {
        try {
            val response = apiService.getWeightData(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load weight data")))
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

    override fun saveWeightReading(weight: String, date: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.saveWeight(weight, date)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to log weight")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error while logging weight")))
            }
        }
    }
}
