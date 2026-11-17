package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorScheduleData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

interface DoctorScheduleRepository {
    fun getScheduleData(date: String? = null): Flow<Result<DoctorScheduleData>>
    fun rescheduleAppointment(appointmentId: Int, newDate: String, newTime: String): Flow<Result<String>>
}

class DoctorScheduleRepositoryImpl : DoctorScheduleRepository {
    private val apiService = RetrofitClient.apiService

    override fun getScheduleData(date: String?): Flow<Result<DoctorScheduleData>> = flow {
        try {
            val response = apiService.getDoctorSchedule(date = date, userId = SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(Result.success(response.data))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to load doctor schedule")))
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

    override fun rescheduleAppointment(
        appointmentId: Int,
        newDate: String,
        newTime: String
    ): Flow<Result<String>> = flow {
        try {
            val response = apiService.rescheduleAppointment(
                appointmentId = appointmentId,
                newDate = newDate,
                newTime = newTime
            )
            if (response.success) {
                emit(Result.success(response.message ?: "Appointment rescheduled successfully"))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to reschedule appointment")))
            }
        } catch (e: Exception) {
            if (e is HttpException && e.code() == 401) {
                SessionManager.notifySessionExpired()
                emit(Result.failure(Exception("Session expired. Please log in again.")))
            } else {
                emit(Result.failure(Exception(e.localizedMessage ?: "Failed to reschedule appointment")))
            }
        }
    }
}
