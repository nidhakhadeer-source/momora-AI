package com.example.SIMATS.data.model

enum class UserRole(
    val label: String,
    val bannerTitle: String,
    val bannerSubtitle: String,
    val createAccountText: String
) {
    PATIENT(
        label = "Patient",
        bannerTitle = "Patient login",
        bannerSubtitle = "Continue your personalized pregnancy journey.",
        createAccountText = "New patient? Create a patient account"
    ),
    DOCTOR(
        label = "Doctor",
        bannerTitle = "Doctor & Clinician login",
        bannerSubtitle = "Access patient monitoring and clinical tools.",
        createAccountText = "New clinician? Request provider access"
    ),
    ADMIN(
        label = "Admin",
        bannerTitle = "Administrator login",
        bannerSubtitle = "Manage system access and healthcare settings.",
        createAccountText = "System Administrator portal"
    )
}
