package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AddDoctorData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface AddDoctorRepository {
    fun getDefaultDoctorFormData(): Flow<Result<AddDoctorData>>
    fun createDoctorAccount(
        name: String,
        email: String,
        specialty: String,
        licenseId: String,
        clinic: String,
        sendActivationEmail: Boolean
    ): Flow<Result<Boolean>>
}

class AddDoctorRepositoryImpl : AddDoctorRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDefaultDoctorFormData(): Flow<Result<AddDoctorData>> = flow {
        try {
            val response = apiService.getAddDoctorFormData()
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load doctor form data")))
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

    override fun createDoctorAccount(
        name: String,
        email: String,
        specialty: String,
        licenseId: String,
        clinic: String,
        sendActivationEmail: Boolean
    ): Flow<Result<Boolean>> = flow {
        try {
            val fields = mapOf(
                "name" to name,
                "email" to email,
                "specialty" to specialty,
                "licenseId" to licenseId,
                "clinic" to clinic,
                "sendActivationEmail" to if (sendActivationEmail) "1" else "0"
            )
            val response = apiService.createDoctorAccount(fields)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to create doctor account")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error while creating doctor account")))
            }
        }
    }
}
