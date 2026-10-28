package com.example.SIMATS.data.model

data class HealthMonitoringData(
    val lastRecordedText: String,
    val bloodPressure: String,
    val bloodPressureUnit: String,
    val heartRate: String,
    val heartRateUnit: String,
    val weight: String,
    val weightDelta: String,
    val temperature: String,
    val temperatureUnit: String,
    val doctorName: String
)
