package com.example.SIMATS.ui.bloodpressure

data class BpReadingItem(
    val dateTimeText: String,
    val pulseText: String,
    val readingText: String
)

data class BloodPressureUiState(
    val title: String = "A clearer pressure picture",
    val subtitle: String = "Home readings · Use a pregnancy-validated cuff",
    val latestTag: String = "LATEST · 26 OCT, 8:05 AM",
    val latestReading: String = "112 / 72",
    val latestDetails: String = "mmHg · Pulse 78 bpm · Seated after 5 min rest",
    val inputSystolic: String = "112",
    val inputDiastolic: String = "72",
    val inputPulse: String = "78 bpm",
    val recentReadings: List<BpReadingItem> = listOf(
        BpReadingItem("25 Oct · 8:00 AM", "Pulse 76 bpm", "110 / 70"),
        BpReadingItem("24 Oct · 8:10 AM", "Pulse 79 bpm", "114 / 74")
    ),
    val thresholdTitle: String = "Know your call thresholds",
    val thresholdDescription: String = "If either number is 140/90 mmHg or higher, contact your maternity team promptly. At 160/110 or higher, seek urgent assessment now. Follow your own care plan.",
    val disclaimerText: String = "Severe headache, vision changes or pain under the ribs? Seek urgent advice regardless of the reading.",
    val selectedTab: Int = 1,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
