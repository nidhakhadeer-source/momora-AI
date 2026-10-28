package com.example.SIMATS.data.model

data class WeightTrackerData(
    val currentWeightKg: String,
    val baselineDeltaText: String,
    val timeRangeTabs: List<String>,
    val yAxisLabels: List<String>,
    val xAxisLabels: List<String>,
    val chartPointsNormalized: List<Pair<Float, Float>>,
    val lastReadingText: String
)
