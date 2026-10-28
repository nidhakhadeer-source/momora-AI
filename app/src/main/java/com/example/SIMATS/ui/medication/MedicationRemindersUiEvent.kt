package com.example.SIMATS.ui.medication

sealed interface MedicationRemindersUiEvent {
    data class OnTabSelected(val index: Int) : MedicationRemindersUiEvent
    data class OnMarkAsTakenClicked(val medicationId: Int) : MedicationRemindersUiEvent
    data class OnNavTabSelected(val index: Int) : MedicationRemindersUiEvent
    data object OnAddMedicationClicked : MedicationRemindersUiEvent
    data object OnSettingsClicked : MedicationRemindersUiEvent
    data object OnBackClicked : MedicationRemindersUiEvent
    data object OnNotificationClicked : MedicationRemindersUiEvent
}
