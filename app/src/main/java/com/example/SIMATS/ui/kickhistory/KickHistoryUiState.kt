package com.example.SIMATS.ui.kickhistory

data class KickBarData(
    val dayLabel: String,
    val count: Int,
    val isToday: Boolean = false
)

data class RecentKickSession(
    val title: String,
    val details: String,
    val statusText: String = "Saved"
)

data class KickHistoryUiState(
    val title: String = "Their own little rhythm",
    val subtitle: String = "Movement journal · 20–26 October",
    val selectedRange: String = "7 days",
    val timeRangeOptions: List<String> = listOf("7 days", "30 days", "All"),
    val chartTitle: String = "Movements recorded per session",
    val barChartData: List<KickBarData> = listOf(
        KickBarData("20", 8),
        KickBarData("21", 10),
        KickBarData("22", 9),
        KickBarData("23", 7),
        KickBarData("24", 11),
        KickBarData("25", 9),
        KickBarData("26", 11, isToday = true)
    ),
    val chartSummaryNote: String = "8 · 10 · 9 · 7 · 11 · 9 · 11 movements. Recording habits vary; this isn't a wellbeing score.",
    val recentSessions: List<RecentKickSession> = listOf(
        RecentKickSession("Today · 8:15 AM", "11 movements · 12 min 18 sec"),
        RecentKickSession("Yesterday · 8:30 AM", "9 movements · 15 min 04 sec")
    ),
    val warningTitle: String = "A change is a reason to call",
    val warningDescription: String = "If movements feel reduced or different from your baby's usual pattern, contact your maternity team now, even if your log looks similar.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
