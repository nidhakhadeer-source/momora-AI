package com.example.SIMATS.ui.duedate

data class DueDateUiState(
    val stepText: String = "GETTING TO KNOW YOU - 3 OF 5",
    val progress: Float = 0.6f,
    val selectedCalculationMethod: String = "Last period",
    val calculationMethods: List<String> = listOf("Last period", "Due date", "Ultrasound"),
    val lastPeriodDate: String = "11 April 2026",
    val cycleLength: String = "28 days",
    val cycleLengthOptions: List<String> = listOf("21 days", "28 days", "30 days", "35 days"),
    val estimatedDueDate: String = "16 January 2027",
    val gestationalAgeText: String = "28 weeks + 2 days today",
    val disclaimerText: String = "Based on a 28-day cycle. Your clinician may adjust this after an ultrasound.",
    val todayEstimateText: String = "Today: 26 October 2026. This is an estimate, not a prediction of your baby's arrival.",
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
