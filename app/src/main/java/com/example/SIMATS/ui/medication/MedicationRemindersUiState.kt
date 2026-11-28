package com.example.SIMATS.ui.medication

import com.example.SIMATS.data.model.MedicationItem
import com.example.SIMATS.data.model.MedicationReminderData

data class MedicationRemindersUiState(
    val data: MedicationReminderData? = null,
    val selectedTab: Int = 0, // 0 = Today, 1 = Schedule, 2 = History
    val selectedScheduleDayIndex: Int = 0,
    val selectedNavIndex: Int = 3,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val userActionMessage: String? = null,
    val isNotificationsEnabled: Boolean = true,
    val showPermissionRationaleDialog: Boolean = false,
    val showNotificationToggleDialog: Boolean = false,
    val showAddEditDialog: Boolean = false,
    val medicationToEdit: MedicationItem? = null,
    val showDiscontinueConfirmDialog: Boolean = false,
    val medicationToDiscontinue: MedicationItem? = null,
    val isSubmittingAction: Boolean = false
)
