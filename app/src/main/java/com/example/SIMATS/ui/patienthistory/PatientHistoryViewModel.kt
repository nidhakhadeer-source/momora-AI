package com.example.SIMATS.ui.patienthistory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.PatientReportItem
import com.example.SIMATS.data.repository.MedicalReportsRepository
import com.example.SIMATS.data.repository.MedicalReportsRepositoryImpl
import com.example.SIMATS.data.repository.PatientHistoryRepository
import com.example.SIMATS.data.repository.PatientHistoryRepositoryImpl
import com.example.SIMATS.navigation.SelectedPatientHolder
import com.example.SIMATS.ui.medicalreports.ReportFileHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class PatientHistoryViewModel(
    private val repository: PatientHistoryRepository = PatientHistoryRepositoryImpl(),
    private val reportsRepository: MedicalReportsRepository = MedicalReportsRepositoryImpl(),
    patientId: String? = SelectedPatientHolder.selectedPatientId,
    patientName: String? = SelectedPatientHolder.selectedPatientName
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        PatientHistoryUiState(patientName = patientName ?: SelectedPatientHolder.selectedPatientName)
    )
    val uiState: StateFlow<PatientHistoryUiState> = _uiState.asStateFlow()

    private var currentPatientId: String? = patientId ?: SelectedPatientHolder.selectedPatientId

    init {
        val id = currentPatientId
        if (!id.isNullOrBlank()) {
            loadHistory(id)
        } else {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "No patient selected"
            )
        }
    }

    fun onEvent(event: PatientHistoryUiEvent) {
        when (event) {
            is PatientHistoryUiEvent.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTab = event.index)
            }
            is PatientHistoryUiEvent.OnMedicationClicked -> {
                _uiState.value = _uiState.value.copy(
                    selectedMedication = event.medication,
                    isMedicationSheetOpen = true
                )
            }
            PatientHistoryUiEvent.OnDismissMedicationSheet -> {
                _uiState.value = _uiState.value.copy(
                    isMedicationSheetOpen = false,
                    selectedMedication = null
                )
            }
            is PatientHistoryUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            is PatientHistoryUiEvent.OnReportClicked -> {
                downloadAndOpenReport(event.report, event.context)
            }
            PatientHistoryUiEvent.OnViewAllReportsClicked -> {
                _uiState.value = _uiState.value.copy(selectedTab = 1)
            }
            PatientHistoryUiEvent.OnOpenHealthMonitoringClicked -> {}
            PatientHistoryUiEvent.OnRetryClicked -> {
                val id = currentPatientId ?: SelectedPatientHolder.selectedPatientId
                if (!id.isNullOrBlank()) {
                    currentPatientId = id
                    loadHistory(id)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "No patient selected"
                    )
                }
            }
            PatientHistoryUiEvent.OnBackClicked -> {}
            PatientHistoryUiEvent.OnNotificationClicked -> {}
        }
    }

    fun downloadAndOpenReport(report: PatientReportItem, context: Context) {
        if (_uiState.value.downloadingReportId == report.id) return

        val safeTitle = report.title.ifBlank { "report_${report.id}" }
            .replace(Regex("[^a-zA-Z0-9_-]"), "_")
        val ext = when {
            report.originalFilename?.lowercase()?.endsWith(".jpg") == true -> "jpg"
            report.originalFilename?.lowercase()?.endsWith(".jpeg") == true -> "jpeg"
            report.originalFilename?.lowercase()?.endsWith(".png") == true -> "png"
            report.fileType?.equals("jpg", ignoreCase = true) == true || report.fileType?.equals("jpeg", ignoreCase = true) == true -> "jpg"
            report.fileType?.equals("png", ignoreCase = true) == true -> "png"
            else -> "pdf"
        }
        val mime = ReportFileHelper.resolveMimeType(
            fileName = report.originalFilename ?: "report.$ext",
            fileType = report.fileType,
            mimeType = report.mimeType
        )

        val destDir = File(context.cacheDir, "reports")
        destDir.mkdirs()
        val destFile = File(destDir, "report_${report.id}_$safeTitle.$ext")

        if (destFile.exists() && destFile.length() > 0) {
            ReportFileHelper.openFile(context, destFile, mime)
            return
        }

        _uiState.value = _uiState.value.copy(downloadingReportId = report.id)
        viewModelScope.launch {
            val flow = if (!report.downloadUrl.isNullOrBlank()) {
                reportsRepository.downloadReportFromUrl(report.downloadUrl, destFile)
            } else {
                reportsRepository.downloadReportFile(report.id, destFile)
            }
            flow.collect { result ->
                _uiState.value = _uiState.value.copy(downloadingReportId = null)
                result.fold(
                    onSuccess = { file ->
                        ReportFileHelper.openFile(context, file, mime)
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            errorMessage = err.message ?: "Failed to open patient report"
                        )
                    }
                )
            }
        }
    }

    fun setPatientId(id: String, name: String? = null) {
        val resolvedName = name ?: SelectedPatientHolder.selectedPatientName
        if (id.isNotBlank() && (id != currentPatientId || _uiState.value.historyData == null)) {
            currentPatientId = id
            SelectedPatientHolder.selectedPatientId = id
            if (!resolvedName.isNullOrBlank()) {
                SelectedPatientHolder.selectedPatientName = resolvedName
            }
            _uiState.value = _uiState.value.copy(patientName = resolvedName ?: _uiState.value.patientName)
            loadHistory(id)
        }
    }

    private fun loadHistory(id: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientHistory(id).collect { result ->
                result.fold(
                    onSuccess = { data ->
                        if (data.patientName.isNotBlank()) {
                            SelectedPatientHolder.selectedPatientName = data.patientName
                        }
                        _uiState.value = _uiState.value.copy(
                            historyData = data,
                            patientName = data.patientName.ifBlank { _uiState.value.patientName },
                            reportsCount = data.reports.size,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load patient history"
                        )
                    }
                )
            }
        }
    }

    class Factory(
        private val repository: PatientHistoryRepository = PatientHistoryRepositoryImpl(),
        private val reportsRepository: MedicalReportsRepository = MedicalReportsRepositoryImpl(),
        private val patientId: String? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientHistoryViewModel(repository, reportsRepository, patientId) as T
        }
    }
}
