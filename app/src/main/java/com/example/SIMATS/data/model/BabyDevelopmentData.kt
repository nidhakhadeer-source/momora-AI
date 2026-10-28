package com.example.SIMATS.data.model

data class BabyDevelopmentData(
    val currentWeek: Int,
    val weekSubtitle: String,
    val lengthCmText: String,
    val lengthSubText: String,
    val weightGramText: String,
    val weightSubText: String,
    val highlights: List<BabyDevelopmentHighlight>
)

data class BabyDevelopmentHighlight(
    val id: Int,
    val title: String,
    val subtitle: String,
    val iconType: HighlightIconType
)

enum class HighlightIconType {
    SOUND,
    MOVEMENT
}
