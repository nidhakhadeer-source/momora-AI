package com.example.SIMATS.ui.adddoctor

sealed interface AddDoctorUiEvent {
    data class OnNameChanged(val name: String) : AddDoctorUiEvent
    data class OnEmailChanged(val email: String) : AddDoctorUiEvent
    data class OnSpecialtyChanged(val specialty: String) : AddDoctorUiEvent
    data class OnLicenseIdChanged(val licenseId: String) : AddDoctorUiEvent
    data class OnClinicChanged(val clinic: String) : AddDoctorUiEvent
    data class OnSendActivationToggled(val isChecked: Boolean) : AddDoctorUiEvent
    data class OnNavTabSelected(val index: Int) : AddDoctorUiEvent
    data object OnCreateAccountClicked : AddDoctorUiEvent
    data object OnBackClicked : AddDoctorUiEvent
    data object OnNotificationClicked : AddDoctorUiEvent
}
