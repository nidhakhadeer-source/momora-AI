package com.example.SIMATS.ui.admindashboard

sealed interface AdminDashboardUiEvent {
    data class OnNavTabSelected(val index: Int) : AdminDashboardUiEvent
    data object OnManageDoctorsClicked : AdminDashboardUiEvent
    data object OnManagePatientsClicked : AdminDashboardUiEvent
    data object OnAddDoctorClicked : AdminDashboardUiEvent
    data object OnNotificationClicked : AdminDashboardUiEvent
}
