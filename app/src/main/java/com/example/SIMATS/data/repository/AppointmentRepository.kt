package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.AppointmentBookingData
import com.example.SIMATS.data.model.CalendarDateItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AppointmentRepository {
    fun getBookingData(): Flow<AppointmentBookingData>
    fun confirmAppointment(doctorName: String, dateText: String, timeText: String, isInPerson: Boolean): Flow<Result<Boolean>>
}

class AppointmentRepositoryImpl : AppointmentRepository {
    override fun getBookingData(): Flow<AppointmentBookingData> = flow {
        emit(
            AppointmentBookingData(
                doctorName = "Dr Amelia Chen",
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
        delay(800)
        emit(Result.success(true))
    }
}
