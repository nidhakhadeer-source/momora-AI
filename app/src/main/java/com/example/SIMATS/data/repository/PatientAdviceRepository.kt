package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.PatientAdviceItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface PatientAdviceRepository {
    fun getPatientAdvice(): Flow<Result<List<PatientAdviceItem>>>
}

class PatientAdviceRepositoryImpl : PatientAdviceRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPatientAdvice(): Flow<Result<List<PatientAdviceItem>>> = flow {
        try {
            val response = apiService.getPatientAdvice()
            if (response.success) {
                emit(Result.success(response.data ?: emptyList()))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load doctor advice")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else if (e is HttpException && e.code() == 403) {
                emit(Result.failure(Exception("Access denied: Patient account required.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network connection error")))
            }
        }
    }
}
