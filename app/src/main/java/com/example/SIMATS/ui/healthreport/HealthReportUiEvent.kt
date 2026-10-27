package com.example.SIMATS.ui.healthreport

sealed interface HealthReportUiEvent {
    data class OnRangeSelected(val range: String) : HealthReportUiEvent
    data class OnToggleMeasurementsAndSymptoms(val enabled: Boolean) : HealthReportUiEvent
    data class OnToggleSleepAndWellbeing(val enabled: Boolean) : HealthReportUiEvent
    data class OnTogglePrivateNotes(val enabled: Boolean) : HealthReportUiEvent
    data object OnPreviewAndExportPdfClicked : HealthReportUiEvent
    data class OnTabSelected(val tabIndex: Int) : HealthReportUiEvent
    data object OnBackClicked : HealthReportUiEvent
    data object OnNavigationHandled : HealthReportUiEvent
}
