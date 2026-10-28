package com.example.SIMATS.ui.medicalreports

import com.example.SIMATS.data.model.ReportCategory

sealed interface MedicalReportsUiEvent {
    data class OnCategorySelected(val category: ReportCategory) : MedicalReportsUiEvent
    data class OnViewReportClicked(val reportId: String) : MedicalReportsUiEvent
    data class OnNavTabSelected(val index: Int) : MedicalReportsUiEvent
    data object OnChooseFileClicked : MedicalReportsUiEvent
    data object OnBackClicked : MedicalReportsUiEvent
    data object OnNotificationClicked : MedicalReportsUiEvent
}
