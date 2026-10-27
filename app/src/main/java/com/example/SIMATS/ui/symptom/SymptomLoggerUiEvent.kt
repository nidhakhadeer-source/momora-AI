package com.example.SIMATS.ui.symptom

sealed interface SymptomLoggerUiEvent {
    data class OnSymptomSelected(val symptom: String) : SymptomLoggerUiEvent
    data class OnSeveritySelected(val severity: String) : SymptomLoggerUiEvent
    data class OnStartTimeChanged(val startTime: String) : SymptomLoggerUiEvent
    data class OnDurationChanged(val duration: String) : SymptomLoggerUiEvent
    data class OnNotesChanged(val notes: String) : SymptomLoggerUiEvent
    data object OnSaveSymptomClicked : SymptomLoggerUiEvent
    data object OnViewWarningSignsClicked : SymptomLoggerUiEvent
    data class OnTabSelected(val tabIndex: Int) : SymptomLoggerUiEvent
    data object OnBackClicked : SymptomLoggerUiEvent
    data object OnNavigationHandled : SymptomLoggerUiEvent
}
