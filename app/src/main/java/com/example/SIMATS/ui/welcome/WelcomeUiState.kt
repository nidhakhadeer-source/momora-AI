package com.example.SIMATS.ui.welcome

import com.example.SIMATS.R

data class FeatureHighlightItem(
    val title: String,
    val subtitle: String,
    val iconRes: Int
)

data class WelcomeUiState(
    val badgeTag: String = "A COMPANION FOR YOUR PREGNANCY",
    val title: String = "Welcome to Momora AI",
    val subtitle: String = "Feel informed, connected and cared for, from your first questions to your baby's arrival.",
    val features: List<FeatureHighlightItem> = listOf(
        FeatureHighlightItem("Care that knows you", "Your journey, your preferences.", R.drawable.ic_heart),
        FeatureHighlightItem("Your health, your choice", "You control who sees your information.", R.drawable.ic_shield_heart)
    ),
    val currentPage: Int = 0,
    val pageCount: Int = 2,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null
)
