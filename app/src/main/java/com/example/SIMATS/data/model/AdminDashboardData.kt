package com.example.SIMATS.data.model

data class AdminDashboardData(
    val lastUpdatedText: String,
    val totalPatientsCount: Int,
    val activePatientsCount: Int,
    val inactivePatientsCount: Int,
    val totalDoctorsCount: Int,
    val activeDoctorsCount: Int,
    val inactiveDoctorsCount: Int,
    val registrationTrendValues: List<Float>,
    val registrationMonths: List<String>
)
