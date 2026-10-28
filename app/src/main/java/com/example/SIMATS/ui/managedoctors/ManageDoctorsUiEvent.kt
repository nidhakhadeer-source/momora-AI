package com.example.SIMATS.ui.managedoctors

sealed interface ManageDoctorsUiEvent {
    data class OnSearchQueryChanged(val query: String) : ManageDoctorsUiEvent
    data class OnFilterTabSelected(val index: Int) : ManageDoctorsUiEvent
    data class OnToggleDoctorStatusClicked(val doctorId: String) : ManageDoctorsUiEvent
    data class OnEditDetailsClicked(val doctorId: String) : ManageDoctorsUiEvent
    data class OnNavTabSelected(val index: Int) : ManageDoctorsUiEvent
    data object OnAddDoctorClicked : ManageDoctorsUiEvent
    data object OnNextPageClicked : ManageDoctorsUiEvent
    data object OnBackClicked : ManageDoctorsUiEvent
    data object OnNotificationClicked : ManageDoctorsUiEvent
}
