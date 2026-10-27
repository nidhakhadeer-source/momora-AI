package com.example.SIMATS.ui.goals

data class GoalsPreferencesUiState(
    val stepText: String = "GETTING TO KNOW YOU - 5 OF 5",
    val progress: Float = 1.0f,
    val selectedTopics: Set<String> = setOf("Baby's growth", "Eating well", "Feeling calmer"),
    val allTopicOptions: List<String> = listOf(
        "Baby's growth",
        "Eating well",
        "Feeling calmer",
        "Staying active",
        "Better sleep",
        "Birth preparation"
    ),
    val isMorningCheckInEnabled: Boolean = true,
    val isEveningReflectionEnabled: Boolean = false,
    val foodPreference: String = "Vegetarian",
    val foodPreferenceOptions: List<String> = listOf("No restrictions", "Vegetarian", "Vegan", "Pescatarian", "Halal", "Kosher"),
    val measurementUnits: String = "Metric · kg, cm, mmol/L",
    val measurementUnitOptions: List<String> = listOf("Metric · kg, cm, mmol/L", "Imperial · lb, in, mg/dL"),
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
