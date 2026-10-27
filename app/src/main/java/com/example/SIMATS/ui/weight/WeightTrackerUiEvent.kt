package com.example.SIMATS.ui.weight

sealed interface WeightTrackerUiEvent {
    data class OnRangeSelected(val range: String) : WeightTrackerUiEvent
    data class OnInputWeightChanged(val weight: String) : WeightTrackerUiEvent
    data object OnSaveWeightClicked : WeightTrackerUiEvent
    data class OnTabSelected(val tabIndex: Int) : WeightTrackerUiEvent
    data object OnBackClicked : WeightTrackerUiEvent
    data object OnNavigationHandled : WeightTrackerUiEvent
}
