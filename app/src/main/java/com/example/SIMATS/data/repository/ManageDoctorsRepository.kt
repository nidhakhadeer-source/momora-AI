package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorAccountItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface ManageDoctorsRepository {
    fun getDoctors(): Flow<Result<List<DoctorAccountItem>>>
    fun toggleDoctorStatus(doctorId: String): Flow<Result<Boolean>>
}

class ManageDoctorsRepositoryImpl : ManageDoctorsRepository {
    private val apiService = RetrofitClient.apiService

    override fun getDoctors(): Flow<Result<List<DoctorAccountItem>>> = flow {
        try {
            val response = apiService.getManageDoctors()
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load doctors list")))
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

    override fun toggleDoctorStatus(doctorId: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.toggleDoctorStatus(doctorId)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to toggle status")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Network error toggling doctor status")))
            }
        }
    }
}
