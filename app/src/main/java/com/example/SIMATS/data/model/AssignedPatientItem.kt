package com.example.SIMATS.data.model

data class AssignedPatientItem(
    val id: String,
    val patientIdText: String,
    val name: String,
    val gestationalAge: String,
    val dueDateText: String? = null,
    val latestVitalsNote: String? = null,
    val visitNote: String? = null,
    val needsReview: Boolean,
    val reviewType: String? = null,
    val isHighlighted: Boolean = false
)
