package com.example.SIMATS.ui.dailyplan

import com.example.SIMATS.R

data class PlanMomentItem(
    val id: String,
    val time: String,
    val title: String,
    val description: String,
    val actionText: String,
    val isCompleted: Boolean,
    val iconRes: Int
)

data class DailyPlanUiState(
    val screenTitle: String = "A gentler kind of to-do",
    val dateSubtitle: String = "Your plan for Monday, 26 October",
    val tagText: String = "PERSONALISED FOR MAYA · 28W 2D",
    val cardTitle: String = "Steady energy. Small steps.",
    val cardDescription: String = "Inspired by your vegetarian preference, low-iron history and today's check-in.",
    val progress: Float = 0.25f,
    val progressFooterText: String = "1 of 4 moments complete · Go at your own pace",
    val momentsList: List<PlanMomentItem> = listOf(
        PlanMomentItem("1", "8:00 AM", "Nourish", "Breakfast and your prenatal vitamin", "Done", true, R.drawable.ic_check_circle),
        PlanMomentItem("2", "12:30 PM", "Build an iron-rich plate", "Lentils, greens and a vitamin C food", "Recipe", false, R.drawable.ic_utensils),
        PlanMomentItem("3", "4:00 PM", "Move comfortably", "Try a 10-minute easy walk, if you feel up to it.", "Start", false, R.drawable.ic_walk),
        PlanMomentItem("4", "8:30 PM", "Make room for rest", "A screen break and a calming routine", "Remind", false, R.drawable.ic_moon)
    ),
    val infoCardTitle: String = "Flexible, not prescriptive",
    val infoCardDescription: String = "Keep following your clinician's iron plan. These suggestions don't replace prescribed treatment.",
    val selectedTab: Int = 2,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
