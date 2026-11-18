package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.SymptomEntryData
import com.example.SIMATS.data.model.SymptomSeverity
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface SymptomRepository {
    fun getSymptomEntryData(): Flow<Result<SymptomEntryData>>
    fun saveSymptom(data: SymptomEntryData): Flow<Result<Boolean>>
}

class SymptomRepositoryImpl : SymptomRepository {
    private val apiService = RetrofitClient.apiService

    override fun getSymptomEntryData(): Flow<Result<SymptomEntryData>> = flow {
        try {
            val response = apiService.getSymptomData(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load symptom data")))
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

    override fun saveSymptom(data: SymptomEntryData): Flow<Result<Boolean>> = flow {
        try {
            val fields = mapOf(
                "symptomName" to data.symptomName,
                "severity" to data.severity.name,
                "startedText" to data.startedText,
                "durationText" to data.durationText
            )
            val response = apiService.saveSymptom(fields)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to log symptom")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while logging symptom")))
        }
    }
}
