package com.example.SIMATS.ui.timeline

import com.example.SIMATS.R

data class TimelineItem(
    val weekNumber: Int,
    val weekTitle: String,
    val description: String,
    val statusText: String,
    val iconRes: Int
)

data class TimelineUiState(
    val title: String = "Your growing story",
    val gestationalAgeSubtitle: String = "28 weeks + 2 days · Due 16 January 2027",
    val selectedTrimester: Int = 3,
    val currentWeekTitle: String = "Week 28 · Hello, third trimester",
    val currentWeekDescription: String = "Baby may open their eyes. You might notice more distinct patterns of movement.",
    val currentWeekProgress: Float = 0.7f,
    val timelineItems: List<TimelineItem> = listOf(
        TimelineItem(27, "Week 27", "Familiar sounds and little stretches", "Complete", R.drawable.ic_check_circle),
        TimelineItem(28, "Week 28", "26 Oct · Your current week", "Now", R.drawable.ic_current_week),
        TimelineItem(29, "Week 29", "Growing stronger, day by day", "Next", R.drawable.ic_sprout),
        TimelineItem(30, "Week 30", "Making room for more growth", "Preview", R.drawable.ic_heart_outline)
    ),
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
