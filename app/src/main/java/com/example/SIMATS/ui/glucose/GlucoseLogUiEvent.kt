package com.example.SIMATS.ui.glucose

sealed interface GlucoseLogUiEvent {
    data class OnBloodGlucoseInputChanged(val value: String) : GlucoseLogUiEvent
    data class OnTimingSelected(val timing: String) : GlucoseLogUiEvent
    data class OnDateTimeChanged(val dateTime: String) : GlucoseLogUiEvent
    data object OnSaveReadingClicked : GlucoseLogUiEvent
    data class OnTabSelected(val tabIndex: Int) : GlucoseLogUiEvent
    data object OnBackClicked : GlucoseLogUiEvent
    data object OnNavigationHandled : GlucoseLogUiEvent
}
