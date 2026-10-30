package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.PatientHealthMonitoringData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface PatientHealthMonitoringRepository {
    fun getPatientHealthMonitoring(patientId: String): Flow<PatientHealthMonitoringData>
}

class PatientHealthMonitoringRepositoryImpl : PatientHealthMonitoringRepository {
    private val apiService = RetrofitClient.apiService

    override fun getPatientHealthMonitoring(patientId: String): Flow<PatientHealthMonitoringData> = flow {
        try {
            val response = apiService.getPatientHealthMonitoring(patientId)
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            PatientHealthMonitoringData(
                patientName = "Maya Patel",
                patientInitials = "MP",
                gestationalAge = "24w 3d",
                dueDateText = "Due 13 Feb 2027",
                patientIdText = "P-13042",
                dateText = "29 Oct 2026",
                latestSystolic = 112,
                latestDiastolic = 72,
                bpReadingTime = "08:15",
                latestWeightKg = 64.2,
                weightReadingTime = "08:10",
                systolicTrends = listOf(114f, 110f, 115f, 112f, 116f, 110f, 112f),
                diastolicTrends = listOf(70f, 72f, 68f, 74f, 71f, 73f, 72f),
                additionalVitalsText = "Pulse 76 bpm · Temperature 36.7 °C. Latest symptom: mild heartburn after meals."
            )
        )
    }
}
