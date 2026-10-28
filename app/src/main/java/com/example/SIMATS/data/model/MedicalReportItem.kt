package com.example.SIMATS.data.model

data class MedicalReportItem(
    val id: String,
    val title: String,
    val dateAndSizeText: String,
    val category: ReportCategory,
    val fileType: String
)

enum class ReportCategory {
    ALL,
    ULTRASOUND,
    LAB_RESULTS
}
