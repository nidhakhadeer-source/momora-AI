package com.example.SIMATS.ui.medication

data class MedicationItem(
    val id: String,
    val timeCategory: String,
    val name: String,
    val dosageInfo: String,
    val isTaken: Boolean,
    val statusText: String,
    val footerNote: String? = null
)

data class MedicationRemindersUiState(
    val title: String = "A little help remembering",
    val subtitle: String = "Monday, 26 October · Your prescribed routine",
    val selectedFilterTab: String = "Today",
    val filterTabs: List<String> = listOf("Today", "Schedule", "History"),
    val medications: List<MedicationItem> = listOf(
        MedicationItem(
            id = "1",
            timeCategory = "MORNING · 8:00 AM",
            name = "Prenatal vitamin",
            dosageInfo = "1 tablet · With breakfast",
            isTaken = true,
            statusText = "Taken ✓",
            footerNote = "Marked taken today at 8:03 AM"
        ),
        MedicationItem(
            id = "2",
            timeCategory = "EVENING · 6:00 PM",
            name = "Iron supplement",
            dosageInfo = "1 prescribed tablet · Dose as labelled",
            isTaken = false,
            statusText = "Due later",
            footerNote = "Added from your clinician's plan. Follow the label and timing instructions."
        )
    ),
    val isRemindersEnabled: Boolean = true,
    val carePlanTitle: String = "Your care plan comes first",
    val carePlanDescription: String = "Momora doesn't prescribe or change doses. Ask your pharmacist about timing, missed doses or taking supplements together.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
