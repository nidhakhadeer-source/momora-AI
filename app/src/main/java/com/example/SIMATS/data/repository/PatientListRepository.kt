package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AssignedPatientItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface PatientListRepository {
    fun getAssignedPatients(): Flow<Result<List<AssignedPatientItem>>>
}

class PatientListRepositoryImpl : PatientListRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAssignedPatients(): Flow<Result<List<AssignedPatientItem>>> = flow {
        try {
            val response = apiService.getAssignedPatients(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load assigned patients")))
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
