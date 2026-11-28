package com.example.SIMATS.data.model

data class DoctorPatientSummaryData(
    val patientId: Int,
    val patientName: String,
    val patientCode: String? = null,
    val gestationalAge: String? = null,
    val dueDateText: String? = null,
    val summaryText: String,
    val sections: DoctorPatientSummarySections? = null,
    val generatedAt: String? = null,
    val modelUsed: String? = null,
    val disclaimer: String? = null
)

data class DoctorPatientSummarySections(
    val pregnancyOverview: String? = null,
    val medicalHealthProfile: String? = null,
    val recentSymptomsConcerns: String? = null,
    val availableHealthMeasurements: String? = null,
    val medicationCareSchedule: String? = null,
    val recentVisitsFollowup: String? = null,
    val informationNotRecorded: String? = null
)
