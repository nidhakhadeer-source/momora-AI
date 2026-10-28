package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.BloodPressureData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface BloodPressureRepository {
    fun getBloodPressureData(): Flow<BloodPressureData>
    fun saveBloodPressureReading(systolic: String, diastolic: String): Flow<Result<Boolean>>
}

class BloodPressureRepositoryImpl : BloodPressureRepository {
    override fun getBloodPressureData(): Flow<BloodPressureData> = flow {
        emit(
            BloodPressureData(
                currentReadingText = "112 / 72 mmHg",
                timestampText = "27 Oct 2026 · 08:15 · Pulse 78 bpm",
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
        delay(800)
        emit(Result.success(true))
    }
}
