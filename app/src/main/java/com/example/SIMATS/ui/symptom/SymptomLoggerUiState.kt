package com.example.SIMATS.ui.symptom

data class SymptomLoggerUiState(
    val title: String = "What are you noticing?",
    val subtitle: String = "A simple record can make care conversations easier.",
    val selectedSymptom: String = "Heartburn",
    val symptomOptions: List<String> = listOf("Heartburn", "Backache", "Nausea", "Swelling", "Headache", "Something else"),
    val selectedSeverity: String = "Mild",
    val severityOptions: List<String> = listOf("Mild", "Moderate", "Severe"),
    val startTime: String = "26 October · 1:30 PM",
    val duration: String = "About 20 minutes",
    val notes: String = "After lunch. Improved when sitting upright.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
