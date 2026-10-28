package com.example.SIMATS.ui.doctoradvice

sealed interface DoctorAdviceUiEvent {
    data class OnSubjectChanged(val subject: String) : DoctorAdviceUiEvent
    data class OnAdviceTextChanged(val text: String) : DoctorAdviceUiEvent
    data class OnSaveCopyToggled(val isChecked: Boolean) : DoctorAdviceUiEvent
    data class OnNavTabSelected(val index: Int) : DoctorAdviceUiEvent
    data object OnSendAdviceClicked : DoctorAdviceUiEvent
    data object OnBackClicked : DoctorAdviceUiEvent
    data object OnNotificationClicked : DoctorAdviceUiEvent
}
