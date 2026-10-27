package com.example.SIMATS.ui.hydration

data class SipEntryItem(
    val id: String,
    val timeText: String,
    val amountText: String
)

data class HydrationLogUiState(
    val title: String = "One sip at a time",
    val dateSubtitle: String = "Monday, 26 October",
    val currentLoggedLiters: Float = 1.25f,
    val goalLiters: Float = 2.0f,
    val progress: Float = 0.625f,
    val progressFooterText: String = "750 mL to your personal goal · Edit goal",
    val selectedAddAmountMl: Int = 250,
    val quickAddOptionsMl: List<Int> = listOf(150, 250, 500),
    val sipsList: List<SipEntryItem> = listOf(
        SipEntryItem("1", "9:30 AM · Water", "250 mL"),
        SipEntryItem("2", "8:00 AM · Water", "500 mL"),
        SipEntryItem("3", "7:00 AM · Water", "500 mL")
    ),
    val isRemindersEnabled: Boolean = true,
    val reminderScheduleText: String = "Every 2 hours · 8:00 AM–8:00 PM",
    val disclaimerText: String = "This is your chosen goal, not a clinical prescription. Follow any fluid advice from your care team.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
