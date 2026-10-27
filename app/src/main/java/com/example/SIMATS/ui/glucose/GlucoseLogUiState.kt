package com.example.SIMATS.ui.glucose

data class GlucoseLogItem(
    val title: String,
    val timeUnitText: String,
    val valueText: String
)

data class GlucoseLogUiState(
    val title: String = "Keep your readings together",
    val subtitle: String = "Glucose journal · Monday, 26 October",
    val infoTitle: String = "For clinician-directed monitoring",
    val infoDescription: String = "Log only if your team has asked you to. Your results do not diagnose gestational diabetes.",
    val bloodGlucoseInput: String = "4.6 mmol/L",
    val selectedTiming: String = "Fasting",
    val timingOptions: List<String> = listOf("Fasting", "1h after meal", "2h after meal"),
    val dateTimeInput: String = "26 October · 7:15 AM",
    val latestReadings: List<GlucoseLogItem> = listOf(
        GlucoseLogItem("Today · Fasting", "7:15 AM · mmol/L", "4.6"),
        GlucoseLogItem("25 Oct · 1h after lunch", "1:30 PM · mmol/L", "6.5")
    ),
    val disclaimerText: String = "Personal targets are not set. Ask your care team what range and testing schedule apply to you.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
