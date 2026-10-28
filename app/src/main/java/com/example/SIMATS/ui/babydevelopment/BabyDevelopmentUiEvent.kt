package com.example.SIMATS.ui.babydevelopment

sealed interface BabyDevelopmentUiEvent {
    data class OnWeekChanged(val week: Int) : BabyDevelopmentUiEvent
    data class OnNavTabSelected(val index: Int) : BabyDevelopmentUiEvent
    data object OnPreviousWeekClicked : BabyDevelopmentUiEvent
    data object OnNextWeekClicked : BabyDevelopmentUiEvent
    data object OnTimelineClicked : BabyDevelopmentUiEvent
    data object OnBackClicked : BabyDevelopmentUiEvent
    data object OnNotificationClicked : BabyDevelopmentUiEvent
    data class OnHighlightClicked(val highlightId: Int) : BabyDevelopmentUiEvent
}
