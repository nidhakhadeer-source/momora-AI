package com.example.SIMATS.data.model

data class DoctorScheduleData(
    val doctorName: String,
    val dateText: String,
    val dateTabs: List<String>,
    val mainAppointment: DoctorMainAppointment,
    val otherAppointments: List<DoctorCompactAppointment>,
    val visitsSummaryNote: String
)

data class DoctorMainAppointment(
    val timeRange: String,
    val statusText: String,
    val patientName: String,
    val patientSubtitle: String,
    val patientIdText: String,
    val notesText: String
)

data class DoctorCompactAppointment(
    val id: String,
    val patientName: String,
    val timeText: String,
    val visitTypeAndMode: String,
    val statusText: String,
    val isVideo: Boolean
)
