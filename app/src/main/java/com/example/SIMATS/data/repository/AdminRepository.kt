package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.AdminDashboardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AdminRepository {
    fun getAdminDashboardData(): Flow<AdminDashboardData>
}

class AdminRepositoryImpl : AdminRepository {
    override fun getAdminDashboardData(): Flow<AdminDashboardData> = flow {
        emit(
            AdminDashboardData(
                lastUpdatedText = "Administrator · Updated 27 Oct 2026, 09:30",
                totalPatientsCount = 128,
                activePatientsCount = 128,
                inactivePatientsCount = 0,
                totalDoctorsCount = 12,
                activeDoctorsCount = 10,
                inactiveDoctorsCount = 2,
                registrationTrendValues = listOf(10f, 18f, 22f, 38f),
                registrationMonths = listOf("Jul", "Aug", "Sep", "Oct")
            )
        )
    }
}
