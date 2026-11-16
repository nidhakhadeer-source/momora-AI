package com.example.SIMATS.data.model

data class WeightTrackerData(
    val currentWeightKg: String? = null,
    val baselineDeltaText: String? = null,
    val timeRangeTabs: List<String> = emptyList(),
    val yAxisLabels: List<String> = emptyList(),
    val xAxisLabels: List<String> = emptyList(),
    val chartPointsNormalized: List<ChartPoint> = emptyList(),
    val lastReadingText: String? = null
)
