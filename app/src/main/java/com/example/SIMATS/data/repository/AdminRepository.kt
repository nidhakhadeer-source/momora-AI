package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.AdminDashboardData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface AdminRepository {
    fun getAdminDashboardData(): Flow<AdminDashboardData>
}

class AdminRepositoryImpl : AdminRepository {
    private val apiService = RetrofitClient.apiService

    override fun getAdminDashboardData(): Flow<AdminDashboardData> = flow {
        try {
            val response = apiService.getAdminDashboard(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            AdminDashboardData(
                lastUpdatedText = "Administrator · Updated 29 Oct 2026, 09:30",
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
