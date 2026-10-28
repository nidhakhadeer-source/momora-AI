package com.example.SIMATS.data.model

data class PatientMedicalHistoryData(
    val patientName: String,
    val patientInitials: String,
    val gestationalAge: String,
    val dueDateText: String,
    val patientIdText: String,
    val clinicalBackgroundText: String,
    val patientConfirmedDateText: String,
    val medications: List<PatientMedicationItem>,
    val recentCareRecordTitle: String,
    val recentCareRecordDetails: String
)

data class PatientMedicationItem(
    val id: String,
    val name: String,
    val dosageDetails: String,
    val isTakenToday: Boolean
)
