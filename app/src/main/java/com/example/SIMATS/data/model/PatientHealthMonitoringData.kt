package com.example.SIMATS.data.model

data class PatientHealthMonitoringData(
    val patientName: String,
    val patientInitials: String,
    val gestationalAge: String,
    val dueDateText: String,
    val patientIdText: String,
    val dateText: String,
    val latestSystolic: Int,
    val latestDiastolic: Int,
    val bpReadingTime: String,
    val latestWeightKg: Double,
    val weightReadingTime: String,
    val systolicTrends: List<Float>,
    val diastolicTrends: List<Float>,
    val additionalVitalsText: String
)
