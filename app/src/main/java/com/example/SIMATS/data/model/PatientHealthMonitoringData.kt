package com.example.SIMATS.data.model

data class PatientHealthMonitoringData(
    val patientName: String,
    val patientInitials: String,
    val gestationalAge: String,
    val dueDateText: String,
    val patientIdText: String,
    val dateText: String,
    val days: Int = 7,
    val latestSystolic: Int? = null,
    val latestDiastolic: Int? = null,
    val bpReadingTime: String? = null,
    val latestWeightKg: Double? = null,
    val weightReadingTime: String? = null,
    val systolicTrends: List<Float> = emptyList(),
    val diastolicTrends: List<Float> = emptyList(),
    val pulse: Int? = null,
    val temperature: Double? = null,
    val temperatureUnit: String? = null,
    val latestSymptom: String? = null,
    val additionalVitalsText: String? = null
)
