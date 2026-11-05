package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorAdviceData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface DoctorAdviceRepository {
    fun getAdviceData(patientId: String): Flow<Result<DoctorAdviceData>>
    fun sendAdvice(subject: String, adviceText: String, saveCopy: Boolean, patientId: String = "1"): Flow<Result<Boolean>>
}

class DoctorAdviceRepositoryImpl : DoctorAdviceRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAdviceData(patientId: String): Flow<Result<DoctorAdviceData>> = flow {
        try {
            val response = apiService.getDoctorAdviceData(patientId)
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load doctor advice data")))
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

    override fun sendAdvice(
        subject: String,
        adviceText: String,
        saveCopy: Boolean,
        patientId: String
    ): Flow<Result<Boolean>> = flow {
        try {
            val fields = mapOf(
                "subject" to subject,
                "adviceText" to adviceText,
                "saveCopy" to if (saveCopy) "1" else "0",
                "patientId" to patientId
            )
            val response = apiService.sendDoctorAdvice(fields)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to send advice")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error while sending advice")))
            }
        }
    }
}
