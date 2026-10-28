package com.example.SIMATS.data.model

data class DoctorAdviceData(
    val doctorName: String,
    val patientName: String,
    val patientInitials: String,
    val gestationalAge: String,
    val dueDateText: String,
    val patientIdText: String,
    val patientMessageTimeText: String,
    val patientMessageQuote: String,
    val defaultSubject: String,
    val defaultAdviceText: String
)
