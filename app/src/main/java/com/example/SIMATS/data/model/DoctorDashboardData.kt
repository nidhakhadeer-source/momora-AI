package com.example.SIMATS.data.model

data class DoctorDashboardData(
    val doctorName: String,
    val clinicName: String,
    val dateText: String,
    val assignedPatientsCount: Int,
    val visitsTodayCount: Int,
    val upcomingVisitsCount: Int,
    val completedVisitsCount: Int,
    val toReviewCount: Int,
    val reviewItems: List<DoctorReviewItem>,
    val nextAppointment: DoctorNextAppointment
)

data class DoctorReviewItem(
    val id: String,
    val patientName: String,
    val gestationalAge: String,
    val reportType: String
)

data class DoctorNextAppointment(
    val patientName: String,
    val timeText: String,
    val visitType: String,
    val mode: String,
    val futureNote: String
)
