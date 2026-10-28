package com.example.SIMATS.data.model

data class OnboardingFeature(
    val id: Int,
    val title: String,
    val description: String,
    val iconType: OnboardingIconType
)

enum class OnboardingIconType {
    HEART,
    SHIELD
}
