package com.example.SIMATS.data.model

data class PatientAccountItem(
    val id: String,
    val name: String,
    val patientIdText: String,
    val assignedDoctorText: String,
    val emailText: String? = null,
    val isActive: Boolean,
    val isHighlighted: Boolean = false
)
