package com.example.SIMATS.ui.contraction

data class ContractionRecord(
    val id: String,
    val durationText: String,
    val intervalText: String,
    val timestampText: String
)

data class ContractionTimerUiState(
    val title: String = "Time it. Keep your team close.",
    val subtitle: String = "28 weeks + 2 days · Timer ready",
    val timerDisplayText: String = "00:00",
    val timerStatusLabel: String = "ready when you are",
    val isContractionActive: Boolean = false,
    val recordedContractions: List<ContractionRecord> = emptyList(),
    val warningTitle: String = "Before 37 weeks? Call for advice",
    val warningDescription: String = "Regular tightenings, period-like pain, backache or leaking fluid can be signs of preterm labour. Contact your maternity team now; don't wait for a pattern.",
    val disclaimerText: String = "A timer can't tell whether you're in labour. Follow your team's advice.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
