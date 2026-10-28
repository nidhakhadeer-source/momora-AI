package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.DailyCareItem
import com.example.SIMATS.data.model.PatientDashboardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DashboardRepository {
    fun getPatientDashboardData(): Flow<PatientDashboardData>
}

class DashboardRepositoryImpl : DashboardRepository {
    override fun getPatientDashboardData(): Flow<PatientDashboardData> = flow {
        emit(
            PatientDashboardData(
                patientName = "Maya",
                dateText = "Tuesday, 27 Oct 2026 · A little care, every day.",
                trimesterText = "SECOND TRIMESTER",
                gestationalAgeText = "24 weeks, 3 days",
                dueDateText = "Due 13 Feb 2027 · Pregnancy details ➔",
                progressPercent = 0.60f,
                bloodPressureValue = "112/72",
                bloodPressureTime = "mmHg · Today, 08:15",
                weightValue = "64.2",
                weightTime = "kg · Today, 08:10",
                dailyCareItems = listOf(
                    DailyCareItem(
                        id = 1,
                        title = "Prenatal vitamin",
                        subtitle = "1 tablet · Today at 08:00",
                        isTaken = true,
                        isAppointment = false
                    ),
                    DailyCareItem(
                        id = 2,
                        title = "Dr Amelia Chen",
                        subtitle = "29 Oct 2026 · 10:30 · In-person",
                        isTaken = false,
                        isAppointment = true
                    )
                )
            )
        )
    }
}
