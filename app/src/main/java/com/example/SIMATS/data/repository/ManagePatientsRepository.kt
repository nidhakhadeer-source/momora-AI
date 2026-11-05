package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.PatientAccountItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface ManagePatientsRepository {
    fun getPatientAccounts(): Flow<Result<List<PatientAccountItem>>>
    fun togglePatientAccess(patientId: String): Flow<Result<Boolean>>
}

class ManagePatientsRepositoryImpl : ManagePatientsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPatientAccounts(): Flow<Result<List<PatientAccountItem>>> = flow {
        try {
            val response = apiService.getManagePatients()
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load patient accounts")))
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

    override fun togglePatientAccess(patientId: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.togglePatientAccess(patientId)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to update access")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error updating patient access")))
            }
        }
    }
}
