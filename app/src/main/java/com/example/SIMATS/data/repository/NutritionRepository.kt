package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.NutritionPlanData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface NutritionRepository {
    fun getNutritionPlan(): Flow<Result<NutritionPlanData>>
}

class NutritionRepositoryImpl : NutritionRepository {
    private val apiService = RetrofitClient.apiService

    override fun getNutritionPlan(): Flow<Result<NutritionPlanData>> = flow {
        try {
            val response = apiService.getNutritionPlan(2)
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load nutrition plan")))
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
