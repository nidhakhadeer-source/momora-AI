package com.example.SIMATS.ui.weight

data class WeightBarData(
    val dateLabel: String,
    val weightKg: Float,
    val isLatest: Boolean = false
)

data class WeightEntryItem(
    val dateTimeText: String,
    val gestationalAgeText: String,
    val weightText: String
)

data class WeightTrackerUiState(
    val title: String = "Your changing body",
    val subtitle: String = "Weight journal · 28 weeks + 2 days",
    val latestWeight: String = "66.4",
    val latestWeightDate: String = "Latest · 26 Oct",
    val weightGain: String = "+6.4",
    val startingWeightInfo: String = "kg · starting at 60.0",
    val selectedRange: String = "Month",
    val rangeOptions: List<String> = listOf("Month", "Trimester", "All"),
    val barChartData: List<WeightBarData> = listOf(
        WeightBarData("28 Sep", 65.2f),
        WeightBarData("5 Oct", 65.5f),
        WeightBarData("12 Oct", 65.8f),
        WeightBarData("19 Oct", 66.1f),
        WeightBarData("26 Oct", 66.4f, isLatest = true)
    ),
    val chartSummaryText: String = "65.2 → 66.4 kg this month · Weekly entries",
    val inputWeightText: String = "66.4 kg",
    val recentEntries: List<WeightEntryItem> = listOf(
        WeightEntryItem("26 October · 8:00 AM", "28 weeks + 2 days", "66.4 kg"),
        WeightEntryItem("19 October · 8:10 AM", "27 weeks + 2 days", "66.1 kg")
    ),
    val disclaimerText: String = "Healthy weight changes are individual. Your care team can help you interpret trends without focusing on a single number.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
