package com.example.SIMATS.data.repository

import com.example.SIMATS.data.model.WeightTrackerData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

interface WeightRepository {
    fun getWeightTrackerData(): Flow<WeightTrackerData>
    fun saveWeightReading(weight: String, date: String): Flow<Result<Boolean>>
}

class WeightRepositoryImpl : WeightRepository {
    override fun getWeightTrackerData(): Flow<WeightTrackerData> = flow {
        emit(
            WeightTrackerData(
                currentWeightKg = "64.2",
                baselineDeltaText = "+5.2 kg from 59.0 kg baseline · 27 Oct 2026, 08:10",
                timeRangeTabs = listOf("4 weeks", "Pregnancy"),
                yAxisLabels = listOf("65", "63", "61", "59"),
                xAxisLabels = listOf("W16", "W18", "W20", "W22", "W24"),
                chartPointsNormalized = listOf(
                    0.00f to 0.88f,
                    0.25f to 0.72f,
                    0.50f to 0.48f,
                    0.75f to 0.30f,
                    1.00f to 0.12f
                ),
                lastReadingText = "Previous - 63.8 kg on 20 Oct 2026 · Change +0.4 kg"
            )
        )
    }

    override fun saveWeightReading(weight: String, date: String): Flow<Result<Boolean>> = flow {
        delay(800)
        emit(Result.success(true))
    }
}
