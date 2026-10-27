package com.example.SIMATS.ui.home

data class HomeUiState(
    val userName: String = "Maya",
    val dateGreetingText: String = "Monday, 26 October · A new day together",
    val trimesterText: String = "YOUR THIRD TRIMESTER",
    val gestationalAgeWeeksText: String = "28 weeks",
    val gestationalAgeDaysText: String = "+ 2 days of growing together",
    val babySizeComparison: String = "Baby is about the size of an aubergine.",
    val progress: Float = 0.7f,
    val dueDetailsText: String = "Due 16 Jan 2027 · About 12 weeks to go",
    val waterLogged: String = "1.25",
    val waterGoal: String = "of 2.0 L",
    val sleepLogged: String = "7h 10m",
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null
)
