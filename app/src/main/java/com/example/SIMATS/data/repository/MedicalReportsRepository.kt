package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface MedicalReportsRepository {
    fun getReports(): Flow<Result<List<MedicalReportItem>>>
    fun uploadReport(fileName: String): Flow<Result<Boolean>>
}

class MedicalReportsRepositoryImpl : MedicalReportsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getReports(): Flow<Result<List<MedicalReportItem>>> = flow {
        try {
            val response = apiService.getReports(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load medical reports")))
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

    override fun uploadReport(fileName: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.uploadReport(fileName)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to upload report")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error uploading report")))
        }
    }
}
