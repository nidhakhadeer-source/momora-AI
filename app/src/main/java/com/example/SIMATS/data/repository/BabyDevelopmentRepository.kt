package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.BabyDevelopmentData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface BabyDevelopmentRepository {
    fun getDevelopmentData(week: Int): Flow<Result<BabyDevelopmentData>>
}

class BabyDevelopmentRepositoryImpl : BabyDevelopmentRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDevelopmentData(week: Int): Flow<Result<BabyDevelopmentData>> = flow {
        try {
            val response = apiService.getBabyDevelopment(week)
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load baby development data")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Check your connection")))
            }
        }
    }
}
