package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.PatientMedicalHistoryData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface PatientHistoryRepository {
    fun getPatientHistory(patientId: String): Flow<Result<PatientMedicalHistoryData>>
}

class PatientHistoryRepositoryImpl : PatientHistoryRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPatientHistory(patientId: String): Flow<Result<PatientMedicalHistoryData>> = flow {
        try {
            val response = apiService.getPatientHistory(patientId)
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load patient medical history")))
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
