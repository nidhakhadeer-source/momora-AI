package com.example.SIMATS.data.model

data class PatientProfileData(
    val avatarInitials: String,
    val patientName: String,
    val patientId: String,
    val fullName: String,
    val email: String,
    val dateOfBirth: String,
    val bloodGroup: String,
    val isCareTeamSharingOn: Boolean,
    val careTeamDoctorName: String,
    val reminderSummary: String
)
