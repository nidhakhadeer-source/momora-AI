package com.example.SIMATS.ui.doctorprofile

sealed interface DoctorProfileUiEvent {
    data class OnFullNameChanged(val value: String) : DoctorProfileUiEvent
    data class OnSpecialtyChanged(val value: String) : DoctorProfileUiEvent
    data class OnLicenseIdChanged(val value: String) : DoctorProfileUiEvent
    data class OnClinicChanged(val value: String) : DoctorProfileUiEvent
    data class OnGenderChanged(val value: String) : DoctorProfileUiEvent
    data class OnPhoneChanged(val value: String) : DoctorProfileUiEvent
    data class OnAddressChanged(val value: String) : DoctorProfileUiEvent
    data class OnQualificationChanged(val value: String) : DoctorProfileUiEvent
    data class OnExperienceYearsChanged(val value: String) : DoctorProfileUiEvent
    data class OnPhotoUrlChanged(val value: String) : DoctorProfileUiEvent
    data object OnToggleEditMode : DoctorProfileUiEvent
    data object OnSaveClicked : DoctorProfileUiEvent
    data object OnCancelEditClicked : DoctorProfileUiEvent
    data object OnRetryClicked : DoctorProfileUiEvent
    data object OnLogoutClicked : DoctorProfileUiEvent
    data object DismissMessages : DoctorProfileUiEvent
    data class OnNavTabSelected(val index: Int) : DoctorProfileUiEvent
}

