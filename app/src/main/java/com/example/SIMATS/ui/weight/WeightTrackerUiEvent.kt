package com.example.SIMATS.ui.weight

sealed interface WeightTrackerUiEvent {
    data class OnTimeRangeSelected(val index: Int) : WeightTrackerUiEvent
    data class OnWeightInputChanged(val weight: String) : WeightTrackerUiEvent
    data class OnDateInputChanged(val date: String) : WeightTrackerUiEvent
    data class OnNavTabSelected(val index: Int) : WeightTrackerUiEvent
    data object OnSaveReadingClicked : WeightTrackerUiEvent
    data object OnBackClicked : WeightTrackerUiEvent
    data object OnNotificationClicked : WeightTrackerUiEvent
}
