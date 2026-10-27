package com.example.SIMATS.ui.sleepinsights

data class DaySleepBarData(
    val dayLabel: String,
    val hours: Float,
    val isToday: Boolean = false
)

data class SleepInsightsUiState(
    val title: String = "Rest, with room for real life",
    val subtitle: String = "Your week · 20–26 October",
    val lastNightSleep: String = "7h 10m",
    val lastNightTimeRange: String = "10:40 PM–6:20 AM*",
    val todaysMood: String = "Good",
    val todaysEnergy: String = "Energy feels steady",
    val chartTitle: String = "Your sleep this week",
    val weeklySleepData: List<DaySleepBarData> = listOf(
        DaySleepBarData("Tue", 6.5f),
        DaySleepBarData("Wed", 7.0f),
        DaySleepBarData("Thu", 6.0f),
        DaySleepBarData("Fri", 7.5f),
        DaySleepBarData("Sat", 6.8f),
        DaySleepBarData("Sun", 6.5f),
        DaySleepBarData("Mon", 7.17f, isToday = true)
    ),
    val weeklyAverageText: String = "Average 6h 55m · Self-reported sleep, not a medical assessment.",
    val insightTitle: String = "A little more rested today",
    val insightDescription: String = "You recorded good mood after 7h 10m of sleep. A week of entries can reveal patterns, but doesn't explain cause and effect.",
    val gentleIdeaTitle: String = "Tonight's gentle idea",
    val gentleIdeaText: String = "Try a pillow between your knees for comfort.",
    val disclaimerNote: String = "*Includes 30 minutes awake. From 28 weeks, settle to sleep on your side. If you wake on your back, simply turn to your side.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
