package com.example.SIMATS.data.model

data class PregnancyDetailsData(
    val trimesterText: String,
    val gestationalAgeText: String,
    val countdownText: String,
    val estimatedDueDate: String,
    val lastMenstrualPeriod: String,
    val assignedDoctorName: String,
    val assignedDoctorSpecialty: String,
    val nextVisitDate: String,
    val nextVisitClinic: String
)
