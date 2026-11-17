package com.example.SIMATS.data.model

data class DoctorAdviceData(
    val doctorName: String = "",
    val patientName: String = "",
    val patientInitials: String = "",
    val gestationalAge: String = "",
    val dueDateText: String = "",
    val patientIdText: String = "",
    val patientMessageTimeText: String = "",
    val patientMessageQuote: String = "",
    val defaultSubject: String = "",
    val defaultAdviceText: String = "",
    val recentSymptoms: List<DoctorAdviceSymptomItem> = emptyList()
)

data class DoctorAdviceSymptomItem(
    val id: Int = 0,
    val symptomName: String = "",
    val severity: String = "",
    val startedText: String = "",
    val durationText: String = "",
    val aiGuidance: String? = null,
    val createdAt: String = "",
    val timestampFormatted: String = "",
    val relativeTime: String = ""
)
