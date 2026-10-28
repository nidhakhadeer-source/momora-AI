package com.example.SIMATS.ui.managepatients

sealed interface ManagePatientsUiEvent {
    data class OnSearchQueryChanged(val query: String) : ManagePatientsUiEvent
    data class OnFilterTabSelected(val index: Int) : ManagePatientsUiEvent
    data class OnEditAccountClicked(val patientId: String) : ManagePatientsUiEvent
    data class OnManageAccessClicked(val patientId: String) : ManagePatientsUiEvent
    data class OnNavTabSelected(val index: Int) : ManagePatientsUiEvent
    data object OnNextPageClicked : ManagePatientsUiEvent
    data object OnBackClicked : ManagePatientsUiEvent
    data object OnNotificationClicked : ManagePatientsUiEvent
}
