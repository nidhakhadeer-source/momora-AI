package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.HealthMonitoringData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface HealthMonitoringRepository {
    fun getHealthMonitoringData(): Flow<HealthMonitoringData>
}

class HealthMonitoringRepositoryImpl : HealthMonitoringRepository {
    override fun getHealthMonitoringData(): Flow<HealthMonitoringData> = flow {
        emit(
            HealthMonitoringData(
                lastRecordedText = "27 Oct 2026 · Last recorded at 08:15",
                bloodPressure = "112/72",
                bloodPressureUnit = "mmHg",
                heartRate = "78",
                heartRateUnit = "beats per minute",
                weight = "64.2",
                weightDelta = "kg · +0.2 kg from last week",
                temperature = "36.7",
                temperatureUnit = "°C · Oral",
                doctorName = "Dr Amelia Chen"
            )
        )
    }
}
