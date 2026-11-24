package com.example.SIMATS.data.model

data class DoctorAccountItem(
    val id: String,
    val name: String,
    val specialtyAndPatientsText: String,
    val emailAndLicenseText: String? = null,
    val isActive: Boolean,
    val isHighlighted: Boolean = false,
    val email: String? = null,
    val specialty: String? = null,
    val licenseId: String? = null,
    val clinic: String? = null,
    val assignedPatientCount: Int = 0
)
