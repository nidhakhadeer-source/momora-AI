package com.example.SIMATS.data.model

data class PatientRegistrationData(
    val fullName: String,
    val email: String,
    val dateOfBirth: String,
    val password: String,
    val isTermsAgreed: Boolean
)
