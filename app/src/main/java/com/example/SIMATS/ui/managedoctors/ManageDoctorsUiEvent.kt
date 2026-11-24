package com.example.SIMATS.ui.managedoctors

import com.example.SIMATS.data.model.DoctorAccountItem

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
    data object OnDismissNotificationsDialog : ManageDoctorsUiEvent
    data object OnRetryClicked : ManageDoctorsUiEvent

    // Doctor Details Dialog
    data class OnDoctorCardClicked(val doctor: DoctorAccountItem) : ManageDoctorsUiEvent
    data object OnDismissDoctorDetailsDialog : ManageDoctorsUiEvent

    // Deactivation / Activation Confirmation
    data class OnRequestToggleStatus(val doctor: DoctorAccountItem) : ManageDoctorsUiEvent
    data object OnDismissToggleConfirmDialog : ManageDoctorsUiEvent
    data object OnConfirmToggleStatus : ManageDoctorsUiEvent

    // Edit Doctor Dialog
    data class OnOpenEditDoctor(val doctor: DoctorAccountItem) : ManageDoctorsUiEvent
    data object OnDismissEditDoctorDialog : ManageDoctorsUiEvent
    data class OnEditNameChanged(val name: String) : ManageDoctorsUiEvent
    data class OnEditEmailChanged(val email: String) : ManageDoctorsUiEvent
    data class OnEditSpecialtyChanged(val specialty: String) : ManageDoctorsUiEvent
    data class OnEditLicenseIdChanged(val licenseId: String) : ManageDoctorsUiEvent
    data class OnEditClinicChanged(val clinic: String) : ManageDoctorsUiEvent
    data object OnSaveDoctorDetails : ManageDoctorsUiEvent

    // Feedback Alert
    data object OnDismissFeedbackMessage : ManageDoctorsUiEvent
}
