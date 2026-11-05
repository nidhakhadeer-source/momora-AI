package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AppointmentBookingData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface AppointmentRepository {
    fun getBookingData(): Flow<Result<AppointmentBookingData>>
    fun confirmAppointment(doctorName: String, dateText: String, timeText: String, isInPerson: Boolean): Flow<Result<Boolean>>
}

class AppointmentRepositoryImpl : AppointmentRepository {
    private val apiService = RetrofitClient.apiService

    override fun getBookingData(): Flow<Result<AppointmentBookingData>> = flow {
        try {
            val response = apiService.getAppointmentBookingData(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load appointments")))
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

    override fun confirmAppointment(
        doctorName: String,
        dateText: String,
        timeText: String,
        isInPerson: Boolean
    ): Flow<Result<Boolean>> = flow {
        try {
            val fields = mapOf(
                "doctorName" to doctorName,
                "dateText" to dateText,
                "timeText" to timeText,
                "isInPerson" to if (isInPerson) "1" else "0"
            )
            val response = apiService.confirmAppointment(fields)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to confirm appointment")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while booking appointment")))
        }
    }
}
