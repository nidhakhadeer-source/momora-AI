package com.example.SIMATS.ui.healthhistory

data class HealthHistoryUiState(
    val stepText: String = "GETTING TO KNOW YOU - 4 OF 5",
    val progress: Float = 0.8f,
    val selectedDiagnoses: Set<String> = setOf("Anaemia"),
    val allDiagnosisOptions: List<String> = listOf(
        "Anaemia",
        "High blood pressure",
        "Diabetes",
        "Thyroid condition",
        "None",
        "Prefer not to say"
    ),
    val currentMedicines: String = "Prenatal vitamin · Iron supplement",
    val allergies: String = "No known allergies",
    val teamMonitoring: String = "Low iron · Repeat blood test at 28 weeks",
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
