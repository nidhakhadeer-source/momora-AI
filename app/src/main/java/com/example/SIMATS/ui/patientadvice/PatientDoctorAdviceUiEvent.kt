package com.example.SIMATS.ui.patientadvice

sealed interface PatientDoctorAdviceUiEvent {
    data object OnScreenOpened : PatientDoctorAdviceUiEvent
    data object OnRetryClicked : PatientDoctorAdviceUiEvent
    data object OnBackClicked : PatientDoctorAdviceUiEvent
    data object OnNotificationClicked : PatientDoctorAdviceUiEvent
    data class OnNavTabSelected(val index: Int) : PatientDoctorAdviceUiEvent
}
