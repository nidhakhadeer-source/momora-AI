package com.example.SIMATS.data.model

data class PatientDashboardData(
    val patientName: String,
    val dateText: String,
    val trimesterText: String,
    val gestationalAgeText: String,
    val dueDateText: String,
    val progressPercent: Float,
    val bloodPressureValue: String,
    val bloodPressureTime: String,
    val weightValue: String,
    val weightTime: String,
    val dailyCareItems: List<DailyCareItem>
)

data class DailyCareItem(
    val id: Int,
    val title: String,
    val subtitle: String,
    val isTaken: Boolean,
    val isAppointment: Boolean
)
