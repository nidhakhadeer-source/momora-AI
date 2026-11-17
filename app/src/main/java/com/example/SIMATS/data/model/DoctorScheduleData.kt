package com.example.SIMATS.data.model

data class DoctorScheduleData(
    val doctorName: String = "",
    val dateText: String = "",
    val selectedDate: String? = null,
    val dateTabs: List<String> = emptyList(),
    val mainAppointment: DoctorMainAppointment? = null,
    val nextAppointment: DoctorMainAppointment? = null,
    val otherAppointments: List<DoctorCompactAppointment> = emptyList(),
    val appointments: List<DoctorAppointmentItem> = emptyList(),
    val completedVisitsCount: Int = 0,
    val upcomingVisitsCount: Int = 0,
    val totalVisitsCount: Int = 0,
    val visitsSummaryNote: String = ""
)

data class DoctorMainAppointment(
    val id: String? = null,
    val timeRange: String = "",
    val statusText: String = "",
    val patientName: String = "",
    val patientSubtitle: String = "",
    val patientIdText: String = "",
    val notesText: String = ""
)

data class DoctorCompactAppointment(
    val id: String = "",
    val patientId: Int? = null,
    val patientName: String = "",
    val timeText: String = "",
    val visitTypeAndMode: String = "",
    val statusText: String = "",
    val isVideo: Boolean = false
)

data class DoctorAppointmentItem(
    val id: String = "",
    val patientId: Int = 0,
    val patientName: String = "",
    val appointmentDate: String = "",
    val appointmentTime: String = "",
    val timeText: String = "",
    val visitTypeAndMode: String = "",
    val statusText: String = "",
    val isVideo: Boolean = false,
    val locationNotes: String? = null
)
