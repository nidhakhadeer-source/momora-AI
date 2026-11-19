package com.example.SIMATS.ui.patienthistory

import android.content.Context
import com.example.SIMATS.data.model.PatientMedicationItem
import com.example.SIMATS.data.model.PatientReportItem

sealed interface PatientHistoryUiEvent {
    data class OnTabSelected(val index: Int) : PatientHistoryUiEvent
    data class OnMedicationClicked(val medication: PatientMedicationItem) : PatientHistoryUiEvent
    data object OnDismissMedicationSheet : PatientHistoryUiEvent
    data class OnNavTabSelected(val index: Int) : PatientHistoryUiEvent
    data class OnReportClicked(val report: PatientReportItem, val context: Context) : PatientHistoryUiEvent
    data object OnViewAllReportsClicked : PatientHistoryUiEvent
    data object OnOpenHealthMonitoringClicked : PatientHistoryUiEvent
    data object OnRetryClicked : PatientHistoryUiEvent
    data object OnBackClicked : PatientHistoryUiEvent
    data object OnNotificationClicked : PatientHistoryUiEvent
}
