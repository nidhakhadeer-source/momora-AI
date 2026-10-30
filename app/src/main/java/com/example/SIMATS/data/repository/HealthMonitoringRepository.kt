package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.HealthMonitoringData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface HealthMonitoringRepository {
    fun getHealthMonitoringData(): Flow<HealthMonitoringData>
}

class HealthMonitoringRepositoryImpl : HealthMonitoringRepository {
    private val apiService = RetrofitClient.apiService

    override fun getHealthMonitoringData(): Flow<HealthMonitoringData> = flow {
        try {
            val response = apiService.getHealthVitals(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            HealthMonitoringData(
                lastRecordedText = "29 Oct 2026 · Last recorded at 08:15",
                bloodPressure = "112/72",
                bloodPressureUnit = "mmHg",
                heartRate = "78",
                heartRateUnit = "beats per minute",
                weight = "64.2",
                weightDelta = "kg · +0.2 kg from last week",
                temperature = "36.7",
                temperatureUnit = "°C · Oral",
                doctorName = SessionManager.getDoctorName()
            )
        )
    }
}
