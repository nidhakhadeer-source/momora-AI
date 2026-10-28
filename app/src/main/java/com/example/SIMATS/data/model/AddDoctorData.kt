package com.example.SIMATS.data.model

data class AddDoctorData(
    val defaultName: String,
    val defaultEmail: String,
    val defaultSpecialty: String,
    val defaultLicenseId: String,
    val defaultClinic: String,
    val availableClinics: List<String>
)
