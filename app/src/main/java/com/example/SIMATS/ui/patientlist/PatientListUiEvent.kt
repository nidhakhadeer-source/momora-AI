package com.example.SIMATS.ui.patientlist

sealed interface PatientListUiEvent {
    data class OnSearchQueryChanged(val query: String) : PatientListUiEvent
    data class OnFilterTabSelected(val index: Int) : PatientListUiEvent
    data class OnPatientClicked(val patientId: String) : PatientListUiEvent
    data class OnNavTabSelected(val index: Int) : PatientListUiEvent
    data object OnPreviousPageClicked : PatientListUiEvent
    data object OnNextPageClicked : PatientListUiEvent
    data object OnBackClicked : PatientListUiEvent
    data object OnNotificationClicked : PatientListUiEvent
}
