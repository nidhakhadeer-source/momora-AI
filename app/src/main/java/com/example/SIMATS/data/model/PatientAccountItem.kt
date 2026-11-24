package com.example.SIMATS.data.model

data class PatientAccountItem(
    val id: String,
    val name: String,
    val patientIdText: String,
    val assignedDoctorText: String,
    val emailText: String? = null,
    val isActive: Boolean,
    val isHighlighted: Boolean = false,
    val phone: String? = null,
    val bloodGroup: String? = null,
    val dueDate: String? = null,
    val gestationalAge: String? = null,
    val clinic: String? = null,
    val assignedDoctor: String? = null,
    val dateOfBirth: String? = null
)
