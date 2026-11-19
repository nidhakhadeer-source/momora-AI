package com.example.SIMATS.ui.medicalreports

import android.net.Uri
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.ReportCategory

data class MedicalReportsUiState(
    val reports: List<MedicalReportItem> = emptyList(),
    val selectedCategory: ReportCategory = ReportCategory.ALL,
    val selectedNavIndex: Int = 3, // Care tab active
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isUploading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val downloadingReportId: String? = null,

    // Upload dialog state
    val showUploadDialog: Boolean = false,
    val selectedFileUri: Uri? = null,
    val uploadFileName: String = "",
    val uploadFileSizeFormatted: String = "",
    val uploadFileSizeBytes: Long = 0L,
    val uploadTitle: String = "",
    val uploadCategory: ReportCategory = ReportCategory.LAB_RESULTS,
    val uploadDescription: String = "",
    val uploadErrorMessage: String? = null,

    // Report detail bottom sheet state
    val showReportDetailSheet: Boolean = false,
    val selectedReportForDetails: MedicalReportItem? = null
)
