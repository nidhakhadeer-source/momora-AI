package com.example.SIMATS.ui.splash

data class SplashUiState(
    val title: String = "momora AI",
    val headline: String = "A little care. Every day.",
    val subtitle: String = "Personalized pregnancy monitoring and gentle guidance, together.",
    val badgeText: String = "YOUR JOURNEY SUPPORTED",
    val footerText: String = "Private by design. Made with compassion.",
    val currentPage: Int = 0,
    val pageCount: Int = 3,
    val isLoading: Boolean = false,
    val navigateToRoute: String? = null
)
