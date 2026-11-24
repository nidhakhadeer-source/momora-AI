package com.example.SIMATS.ui.managepatients

import com.example.SIMATS.data.model.PatientAccountItem

sealed interface ManagePatientsUiEvent {
    data class OnSearchQueryChanged(val query: String) : ManagePatientsUiEvent
    data class OnFilterTabSelected(val index: Int) : ManagePatientsUiEvent
    data class OnPatientCardClicked(val patient: PatientAccountItem) : ManagePatientsUiEvent
    data class OnRequestToggleAccess(val patient: PatientAccountItem) : ManagePatientsUiEvent
    data object OnConfirmToggleAccess : ManagePatientsUiEvent
    data object OnDismissToggleConfirmDialog : ManagePatientsUiEvent
    data object OnDismissPatientDetailsDialog : ManagePatientsUiEvent
    data object OnDismissFeedbackMessage : ManagePatientsUiEvent
    data class OnEditAccountClicked(val patientId: String) : ManagePatientsUiEvent
    data class OnManageAccessClicked(val patientId: String) : ManagePatientsUiEvent
    data class OnNavTabSelected(val index: Int) : ManagePatientsUiEvent
    data object OnNextPageClicked : ManagePatientsUiEvent
    data object OnBackClicked : ManagePatientsUiEvent
    data object OnNotificationClicked : ManagePatientsUiEvent
    data object OnDismissNotificationsDialog : ManagePatientsUiEvent
    data object OnRetryClicked : ManagePatientsUiEvent
}
