package com.example.SIMATS.ui.medicalreports

import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.ReportCategory

data class MedicalReportsUiState(
    val reports: List<MedicalReportItem> = emptyList(),
    val selectedCategory: ReportCategory = ReportCategory.ALL,
    val selectedNavIndex: Int = 3, // Care tab active
    val isUploading: Boolean = false,
    val errorMessage: String? = null
)
