package com.example.SIMATS.ui.patienthistory

import com.example.SIMATS.data.model.DoctorPatientSummaryData
import com.example.SIMATS.data.model.PatientHealthMonitoringData
import com.example.SIMATS.data.model.PatientMedicalHistoryData
import com.example.SIMATS.data.model.PatientMedicationItem

data class PatientHistoryUiState(
    val historyData: PatientMedicalHistoryData? = null,
    val patientName: String? = null,
    val selectedTab: Int = 0, // 0 = Summary, 1 = Reports, 2 = Visits, 3 = AI Summary
    val reportsCount: Int = 0,
    val selectedMedication: PatientMedicationItem? = null,
    val isMedicationSheetOpen: Boolean = false,
    val selectedNavIndex: Int = 1, // Patients tab active (index 1)
    val isLoading: Boolean = false,
    val downloadingReportId: String? = null,
    val errorMessage: String? = null,
    val summaryData: DoctorPatientSummaryData? = null,
    val isSummaryLoading: Boolean = false,
    val summaryErrorMessage: String? = null,
    val isSummaryGenerated: Boolean = false,
    val healthMonitoringData: PatientHealthMonitoringData? = null,
    val isHealthMonitoringExpanded: Boolean = false
)
