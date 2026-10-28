package com.example.SIMATS.ui.bloodpressure

sealed interface BloodPressureUiEvent {
    data class OnSystolicInputChanged(val value: String) : BloodPressureUiEvent
    data class OnDiastolicInputChanged(val value: String) : BloodPressureUiEvent
    data class OnNavTabSelected(val index: Int) : BloodPressureUiEvent
    data object OnSaveReadingClicked : BloodPressureUiEvent
    data object OnBackClicked : BloodPressureUiEvent
    data object OnNotificationClicked : BloodPressureUiEvent
}
