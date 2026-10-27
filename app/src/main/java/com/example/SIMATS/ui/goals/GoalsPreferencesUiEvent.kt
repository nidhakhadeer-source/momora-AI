package com.example.SIMATS.ui.goals

sealed interface GoalsPreferencesUiEvent {
    data class OnTopicToggled(val topic: String) : GoalsPreferencesUiEvent
    data class OnToggleMorningCheckIn(val enabled: Boolean) : GoalsPreferencesUiEvent
    data class OnToggleEveningReflection(val enabled: Boolean) : GoalsPreferencesUiEvent
    data class OnFoodPreferenceChanged(val foodPref: String) : GoalsPreferencesUiEvent
    data class OnMeasurementUnitsChanged(val units: String) : GoalsPreferencesUiEvent
    data object OnMeetMomoraClicked : GoalsPreferencesUiEvent
    data object OnBackClicked : GoalsPreferencesUiEvent
    data object OnNavigationHandled : GoalsPreferencesUiEvent
}
