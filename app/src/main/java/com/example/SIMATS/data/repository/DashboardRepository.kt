package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DailyCareItem
import com.example.SIMATS.data.model.PatientDashboardData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface DashboardRepository {
    fun getPatientDashboardData(): Flow<PatientDashboardData>
}

class DashboardRepositoryImpl : DashboardRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPatientDashboardData(): Flow<PatientDashboardData> = flow {
        try {
            val response = apiService.getPatientDashboard(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {
            // Graceful fallback to initial values if network error
        }

        // Default initial dashboard state based on session
        emit(
            PatientDashboardData(
                patientName = SessionManager.getUserName().split(" ").firstOrNull() ?: "Maya",
                dateText = "Thursday, 29 Oct 2026 · A little care, every day.",
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
                        title = SessionManager.getDoctorName(),
                        subtitle = "29 Oct 2026 · 10:30 · In-person",
                        isTaken = false,
                        isAppointment = true
                    )
                )
            )
        )
    }
}
