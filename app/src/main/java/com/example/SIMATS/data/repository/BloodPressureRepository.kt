package com.example.SIMATS.data.repository

import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.BloodPressureData
import com.example.SIMATS.data.remote.RetrofitClient
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface BloodPressureRepository {
    fun getBloodPressureData(): Flow<BloodPressureData>
    fun saveBloodPressureReading(systolic: String, diastolic: String): Flow<Result<Boolean>>
}

class BloodPressureRepositoryImpl : BloodPressureRepository {
    private val apiService = RetrofitClient.apiService

    override fun getBloodPressureData(): Flow<BloodPressureData> = flow {
        try {
            val response = apiService.getBloodPressureData(SessionManager.getUserId())
            if (response.success && response.data != null) {
                emit(response.data)
                return@flow
            }
        } catch (_: Exception) {}

        emit(
            BloodPressureData(
                currentReadingText = "112 / 72 mmHg",
                timestampText = "29 Oct 2026 · 08:15 · Pulse 78 bpm",
                yAxisLabels = listOf("120", "100", "80", "60"),
                xAxisLabels = listOf("21 Oct", "23 Oct", "25 Oct", "27 Oct"),
                systolicPointsNormalized = listOf(
                    0.00f to 0.28f,
                    0.33f to 0.35f,
                    0.66f to 0.25f,
                    1.00f to 0.28f
                ),
                diastolicPointsNormalized = listOf(
                    0.00f to 0.78f,
                    0.33f to 0.82f,
                    0.66f to 0.75f,
                    1.00f to 0.78f
                )
            )
        )
    }

    override fun saveBloodPressureReading(systolic: String, diastolic: String): Flow<Result<Boolean>> = flow {
        try {
            val response = apiService.saveBloodPressure(systolic, diastolic)
            if (response.success) {
                emit(Result.success(true))
            } else {
                emit(Result.failure(Exception(response.message ?: "Failed to log blood pressure")))
            }
        } catch (e: Exception) {
            emit(Result.failure(Exception(e.localizedMessage ?: "Network error while logging blood pressure")))
        }
    }
}
