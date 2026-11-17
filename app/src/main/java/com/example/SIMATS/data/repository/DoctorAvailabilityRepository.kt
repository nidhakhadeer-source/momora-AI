package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorAvailabilityResponseData
import com.example.SIMATS.data.model.DoctorAvailabilitySlot
import com.example.SIMATS.data.model.SaveAvailabilityRequest
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface DoctorAvailabilityRepository {
    fun getAvailability(): Flow<Result<DoctorAvailabilityResponseData>>
    fun saveAvailability(weeklyHours: List<DoctorAvailabilitySlot>): Flow<Result<String>>
}

class DoctorAvailabilityRepositoryImpl : DoctorAvailabilityRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAvailability(): Flow<Result<DoctorAvailabilityResponseData>> = flow {
        try {
            val response = apiService.getDoctorAvailability()
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load clinic availability")))
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

    override fun saveAvailability(weeklyHours: List<DoctorAvailabilitySlot>): Flow<Result<String>> = flow {
        try {
            val response = apiService.saveDoctorAvailability(SaveAvailabilityRequest(weeklyHours))
            if (response.success) {
                emit(Result.success(response.message ?: "Availability saved successfully"))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to save availability")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Failed to save availability")))
            }
        }
    }
}
