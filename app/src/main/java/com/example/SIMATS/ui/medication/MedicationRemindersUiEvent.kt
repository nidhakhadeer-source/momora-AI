package com.example.SIMATS.ui.medication

import com.example.SIMATS.data.model.MedicationItem

sealed interface MedicationRemindersUiEvent {
    data class OnTabSelected(val index: Int) : MedicationRemindersUiEvent
    data class OnScheduleDaySelected(val index: Int) : MedicationRemindersUiEvent
    data class OnMarkAsTakenClicked(val medicationId: Int) : MedicationRemindersUiEvent
    data class OnNavTabSelected(val index: Int) : MedicationRemindersUiEvent
    data object OnAddMedicationClicked : MedicationRemindersUiEvent
    data class OnEditMedicationClicked(val medication: MedicationItem) : MedicationRemindersUiEvent
    data class OnDiscontinueMedicationClicked(val medication: MedicationItem) : MedicationRemindersUiEvent
    data object OnConfirmDiscontinue : MedicationRemindersUiEvent
    data object OnDismissDiscontinueDialog : MedicationRemindersUiEvent
    data class OnSaveMedication(
        val medicationId: Int?,
        val name: String,
        val dosage: String,
        val frequency: String,
        val reminderTime: String,
        val instructions: String?,
        val startDate: String?,
        val endDate: String?,
        val doctorName: String?
    ) : MedicationRemindersUiEvent
    data object OnDismissAddEditDialog : MedicationRemindersUiEvent
    data object OnNotificationClicked : MedicationRemindersUiEvent
    data class OnToggleNotifications(val enabled: Boolean) : MedicationRemindersUiEvent
    data object OnDismissNotificationDialog : MedicationRemindersUiEvent
    data object OnDismissPermissionRationale : MedicationRemindersUiEvent
    data object OnPermissionDenied : MedicationRemindersUiEvent
    data object OnPermissionGranted : MedicationRemindersUiEvent
    data object OnRetryClicked : MedicationRemindersUiEvent
    data object OnBackClicked : MedicationRemindersUiEvent
    data object OnClearUserMessage : MedicationRemindersUiEvent
}
