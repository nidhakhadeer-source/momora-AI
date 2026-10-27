package com.example.SIMATS.ui.visitprep

sealed interface VisitPrepUiEvent {
    data object OnEditAppointmentClicked : VisitPrepUiEvent
    data object OnContactDoctorClicked : VisitPrepUiEvent
    data class OnToggleChecklistItem(val id: String) : VisitPrepUiEvent
    data object OnAddQuestionClicked : VisitPrepUiEvent
    data class OnToggleReminder(val enabled: Boolean) : VisitPrepUiEvent
    data object OnPrepareHealthSummaryClicked : VisitPrepUiEvent
    data class OnTabSelected(val tabIndex: Int) : VisitPrepUiEvent
    data object OnBackClicked : VisitPrepUiEvent
    data object OnNavigationHandled : VisitPrepUiEvent
}
