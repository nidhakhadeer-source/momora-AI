package com.example.SIMATS.data.model

data class BloodPressureData(
    val currentReadingText: String,
    val timestampText: String,
    val yAxisLabels: List<String>,
    val xAxisLabels: List<String>,
    val systolicPointsNormalized: List<Pair<Float, Float>>,
    val diastolicPointsNormalized: List<Pair<Float, Float>>
)
