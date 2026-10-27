package com.example.SIMATS.ui.medication

sealed interface MedicationRemindersUiEvent {
    data class OnFilterTabSelected(val tab: String) : MedicationRemindersUiEvent
    data class OnMarkAsTakenClicked(val id: String) : MedicationRemindersUiEvent
    data class OnToggleReminders(val enabled: Boolean) : MedicationRemindersUiEvent
    data object OnAddMedicineClicked : MedicationRemindersUiEvent
    data class OnTabSelected(val tabIndex: Int) : MedicationRemindersUiEvent
    data object OnBackClicked : MedicationRemindersUiEvent
    data object OnNavigationHandled : MedicationRemindersUiEvent
}
