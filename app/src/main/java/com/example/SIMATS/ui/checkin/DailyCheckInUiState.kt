package com.example.SIMATS.ui.checkin

data class DailyCheckInUiState(
    val title: String = "How are you, really?",
    val dateSubtitle: String = "Monday, 26 October · Your daily check-in",
    val selectedMood: String = "Good",
    val moodOptions: List<String> = listOf("Low", "A little flat", "Okay", "Good", "Bright"),
    val selectedEnergy: String = "Steady",
    val energyOptions: List<String> = listOf("Low", "Steady", "Energised"),
    val sleepDuration: String = "7 hours 10 minutes",
    val notesOnMind: String = "Feeling more rested. A little heartburn after lunch.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
