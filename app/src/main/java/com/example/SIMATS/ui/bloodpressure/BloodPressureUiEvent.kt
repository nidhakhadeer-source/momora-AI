package com.example.SIMATS.ui.bloodpressure

sealed interface BloodPressureUiEvent {
    data class OnSystolicChanged(val systolic: String) : BloodPressureUiEvent
    data class OnDiastolicChanged(val diastolic: String) : BloodPressureUiEvent
    data class OnPulseChanged(val pulse: String) : BloodPressureUiEvent
    data object OnSaveReadingClicked : BloodPressureUiEvent
    data class OnTabSelected(val tabIndex: Int) : BloodPressureUiEvent
    data object OnBackClicked : BloodPressureUiEvent
    data object OnNavigationHandled : BloodPressureUiEvent
}
