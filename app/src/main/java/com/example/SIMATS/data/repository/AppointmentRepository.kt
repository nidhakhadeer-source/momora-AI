package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AppointmentBookingData
import com.example.SIMATS.data.model.CalendarDateItem
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AppointmentRepository {
    fun getBookingData(): Flow<AppointmentBookingData>
    fun confirmAppointment(doctorName: String, dateText: String, timeText: String, isInPerson: Boolean): Flow<Result<Boolean>>
}

class AppointmentRepositoryImpl : AppointmentRepository {
    private val apiService = RetrofitClient.apiService

    override fun getBookingData(): Flow<AppointmentBookingData> = flow {
        try {
            val response = apiService.getAppointmentBookingData(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            AppointmentBookingData(
                doctorName = SessionManager.getDoctorName(),
                doctorSpecialty = "Obstetrician · Willow Maternity Clinic",
                monthTitle = "October 2026",
                availableDates = listOf(
                    CalendarDateItem("Tue", 27, false),
                    CalendarDateItem("Wed", 28, false),
                    CalendarDateItem("Thu", 29, true),
                    CalendarDateItem("Fri", 30, false),
                    CalendarDateItem("Sat", 31, false)
                ),
                availableTimeSlots = listOf("09:00", "10:30", "14:00"),
                defaultType = "Routine antenatal check",
                defaultLocationNotes = "Willow Maternity Clinic · Room 3. Bring your pregnancy notes and recent reports."
            )
        )
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
