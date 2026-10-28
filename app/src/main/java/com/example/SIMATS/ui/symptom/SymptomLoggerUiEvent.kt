package com.example.SIMATS.ui.symptom

import com.example.SIMATS.data.model.SymptomSeverity

sealed interface SymptomLoggerUiEvent {
    data class OnSymptomSelected(val symptom: String) : SymptomLoggerUiEvent
    data class OnSeveritySelected(val severity: SymptomSeverity) : SymptomLoggerUiEvent
    data class OnStartedChanged(val started: String) : SymptomLoggerUiEvent
    data class OnDurationChanged(val duration: String) : SymptomLoggerUiEvent
    data class OnDropdownToggled(val expanded: Boolean) : SymptomLoggerUiEvent
    data class OnNavTabSelected(val index: Int) : SymptomLoggerUiEvent
    data object OnSaveSymptomClicked : SymptomLoggerUiEvent
    data object OnBackClicked : SymptomLoggerUiEvent
    data object OnNotificationClicked : SymptomLoggerUiEvent
}
