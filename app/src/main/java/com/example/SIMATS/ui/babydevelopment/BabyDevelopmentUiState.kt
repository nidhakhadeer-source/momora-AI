package com.example.SIMATS.ui.babydevelopment

import com.example.SIMATS.R

data class DevelopmentWonderItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int
)

data class BabyDevelopmentUiState(
    val title: String = "A little more you, every day",
    val weekSubtitle: String = "Week 28 · 28 weeks + 2 days",
    val approxLength: String = "37.6",
    val lengthUnit: String = "cm · head to heel",
    val approxWeight: String = "1.0",
    val weightUnit: String = "kg",
    val wondersList: List<DevelopmentWonderItem> = listOf(
        DevelopmentWonderItem("Opening their eyes", "Eyelids can open and close.", R.drawable.ic_eye),
        DevelopmentWonderItem("Listening to your world", "Familiar voices may be soothing.", R.drawable.ic_ear),
        DevelopmentWonderItem("Finding a rhythm", "Movement patterns become more familiar.", R.drawable.ic_nav_track)
    ),
    val changingBodyTitle: String = "And in your changing body",
    val changingBodyText: String = "Heartburn and tiredness can show up as baby grows. Try smaller meals and take comfortable rest breaks.",
    val disclaimerText: String = "Size estimates are educational averages. Your ultrasound and care team tell your baby's own story.",
    val isBookmarked: Boolean = false,
    val selectedTab: Int = 0,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null,
    val navigateBack: Boolean = false
)
