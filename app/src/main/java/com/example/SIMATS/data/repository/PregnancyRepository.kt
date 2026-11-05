package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.PregnancyDetailsData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface PregnancyRepository {
    fun getPregnancyDetails(): Flow<Result<PregnancyDetailsData>>
    fun savePregnancyDetails(details: PregnancyDetailsData): Flow<Result<Boolean>>
}

class PregnancyRepositoryImpl : PregnancyRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPregnancyDetails(): Flow<Result<PregnancyDetailsData>> = flow {
        try {
            val response = apiService.getPregnancyDetails(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load pregnancy details")))
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

    override fun savePregnancyDetails(details: PregnancyDetailsData): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.savePregnancyDetails(
                edd = details.estimatedDueDate,
                lmp = details.lastMenstrualPeriod
            )
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to save pregnancy details")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while saving pregnancy details")))
        }
    }
}
