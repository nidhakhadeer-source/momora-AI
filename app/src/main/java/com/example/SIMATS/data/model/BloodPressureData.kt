package com.example.SIMATS.data.model

data class BloodPressureData(
    val currentReadingText: String? = null,
    val timestampText: String? = null,
    val yAxisLabels: List<String> = emptyList(),
    val xAxisLabels: List<String> = emptyList(),
    val systolicPointsNormalized: List<ChartPoint> = emptyList(),
    val diastolicPointsNormalized: List<ChartPoint> = emptyList()
)
