package com.example.SIMATS.data.model

data class PatientMedicalHistoryData(
    val patientName: String,
    val patientInitials: String,
    val gestationalAge: String,
    val dueDateText: String,
    val patientIdText: String,
    val clinicalBackgroundText: String,
    val patientConfirmedDateText: String? = null,
    val medications: List<PatientMedicationItem> = emptyList(),
    val reports: List<PatientReportItem> = emptyList(),
    val visits: List<PatientVisitItem> = emptyList(),
    val recentCareRecordTitle: String? = null,
    val recentCareRecordDetails: String? = null
)

data class PatientReportItem(
    val id: String,
    val title: String,
    val reportDate: String? = null,
    val category: String? = null,
    val size: String? = null
)

data class PatientVisitItem(
    val id: String,
    val date: String? = null,
    val time: String? = null,
    val status: String? = null,
    val notes: String? = null
)

data class PatientMedicationItem(
    val id: String,
    val name: String,
    val dosageDetails: String,
    val isTakenToday: Boolean
)
