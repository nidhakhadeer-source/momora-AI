package com.example.SIMATS.data.model

data class SymptomEntryData(
    val symptomName: String,
    val severity: SymptomSeverity,
    val startedText: String,
    val durationText: String,
    val aiGuidanceText: String,
    val availableSymptoms: List<String>
)

enum class SymptomSeverity(val label: String) {
    MILD("Mild"),
    MODERATE("Moderate"),
    SEVERE("Severe")
}
