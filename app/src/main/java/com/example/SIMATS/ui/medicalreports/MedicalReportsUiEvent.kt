package com.example.SIMATS.ui.medicalreports

import android.content.Context
import android.net.Uri
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.ReportCategory

sealed interface MedicalReportsUiEvent {
    data class OnCategorySelected(val category: ReportCategory) : MedicalReportsUiEvent
    data class OnViewReportClicked(val reportId: String) : MedicalReportsUiEvent
    data class OnOpenReport(val report: MedicalReportItem, val context: Context) : MedicalReportsUiEvent
    data class OnShareReport(val report: MedicalReportItem, val context: Context) : MedicalReportsUiEvent
    data class OnReportCardClicked(val report: MedicalReportItem) : MedicalReportsUiEvent

    data class OnFilePicked(val uri: Uri, val context: Context) : MedicalReportsUiEvent
    data class OnUploadTitleChanged(val title: String) : MedicalReportsUiEvent
    data class OnUploadCategoryChanged(val category: ReportCategory) : MedicalReportsUiEvent
    data class OnUploadDescriptionChanged(val description: String) : MedicalReportsUiEvent
    data class OnConfirmUpload(val context: Context) : MedicalReportsUiEvent
    data object OnDismissUploadDialog : MedicalReportsUiEvent

    data object OnDismissReportDetailSheet : MedicalReportsUiEvent
    data object OnDismissMessage : MedicalReportsUiEvent
    data object OnChooseFileClicked : MedicalReportsUiEvent
    data object OnRetryClicked : MedicalReportsUiEvent
    data object OnRefresh : MedicalReportsUiEvent
    data object OnBackClicked : MedicalReportsUiEvent
    data object OnNotificationClicked : MedicalReportsUiEvent
    data class OnNavTabSelected(val index: Int) : MedicalReportsUiEvent
}
