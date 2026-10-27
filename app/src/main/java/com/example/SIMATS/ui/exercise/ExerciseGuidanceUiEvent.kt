package com.example.SIMATS.ui.exercise

sealed interface ExerciseGuidanceUiEvent {
    data class OnIntensityOptionSelected(val option: String) : ExerciseGuidanceUiEvent
    data class OnStepItemClicked(val step: ExerciseStepItem) : ExerciseGuidanceUiEvent
    data object OnBeginSessionClicked : ExerciseGuidanceUiEvent
    data class OnTabSelected(val tabIndex: Int) : ExerciseGuidanceUiEvent
    data object OnBackClicked : ExerciseGuidanceUiEvent
    data object OnNavigationHandled : ExerciseGuidanceUiEvent
}
