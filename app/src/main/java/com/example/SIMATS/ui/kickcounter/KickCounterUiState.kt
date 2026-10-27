package com.example.SIMATS.ui.kickcounter

data class KickCounterUiState(
    val title: String = "Little movements, noticed",
    val dateSubtitle: String = "Today · 28 weeks + 2 days",
    val instructionText: String = "Settle somewhere comfortable. Tap for each distinct movement you feel.",
    val kickCount: Int = 6,
    val sessionTimerText: String = "04:32",
    val isTimerRunning: Boolean = true,
    val warningTitle: String = "Your baby's own pattern matters",
    val warningDescription: String = "There is no universal normal count. If movements are less than usual, have stopped or changed, contact your maternity team immediately. Don't wait for this timer.",
    val disclaimerText: String = "Follow your care team's counting advice. This tool records movements; it cannot confirm wellbeing.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
