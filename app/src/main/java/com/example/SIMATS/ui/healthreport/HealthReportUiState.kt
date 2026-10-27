package com.example.SIMATS.ui.healthreport

data class HealthReportUiState(
    val title: String = "Your story, ready to share",
    val userSubtitle: String = "Maya Patel · 28 weeks + 2 days",
    val selectedTimeRange: String = "7 days",
    val rangeOptions: List<String> = listOf("7 days", "30 days", "Custom"),
    val reportDateTag: String = "REPORT PREVIEW · 20–26 OCT 2026",
    val reportTitle: String = "A week of small observations",
    val latestWeightText: String = "66.4 kg",
    val weightInfoText: String = "26 Oct · Starting weight 60.0 kg",
    val latestBpText: String = "112/72 mmHg",
    val bpInfoText: String = "26 Oct · Pulse 78 bpm",
    val symptomsText: String = "2 entries",
    val symptomsInfoText: String = "Mild heartburn · After lunch",
    val sleepAvgText: String = "6h 55m",
    val sleepInfoText: String = "Self-reported · 7 nights",
    val includeMeasurementsAndSymptoms: Boolean = true,
    val includeSleepAndWellbeing: Boolean = true,
    val includePrivateNotes: Boolean = false,
    val privacyGuardTitle: String = "Nothing is shared automatically",
    val privacyGuardDescription: String = "Preview your PDF, then choose who receives it. Sharing a report doesn't notify your clinician or request urgent care.",
    val selectedTab: Int = 4,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
