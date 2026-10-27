package com.example.SIMATS.ui.visitprep

data class PrepChecklistItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val isCompleted: Boolean,
    val statusText: String
)

data class VisitPrepUiState(
    val title: String = "Your next care conversation",
    val appointmentTitle: String = "28-week antenatal visit",
    val appointmentDate: String = "Wednesday, 28 October",
    val appointmentTimeAndType: String = "10:30–11:00 AM · In person",
    val doctorName: String = "Dr Amira Shah",
    val clinicLocation: String = "Riverside Maternity · Clinic 2",
    val gestationalAgeNote: String = "You'll be 28 weeks + 4 days.",
    val prepChecklist: List<PrepChecklistItem> = listOf(
        PrepChecklistItem("1", "Bring maternity notes", "And your current medicine list", true, "Done"),
        PrepChecklistItem("2", "Include recent health logs", "Weight, blood pressure and symptoms", true, "Done"),
        PrepChecklistItem("3", "Ask about the repeat blood test", "Review low iron and supplement plan", false, "To do")
    ),
    val userQuestions: List<String> = listOf(
        "\"Is my iron level improving?\"",
        "\"What can I safely take for heartburn?\""
    ),
    val isReminderEnabled: Boolean = true,
    val reminderDateText: String = "27 October · 10:30 AM",
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
