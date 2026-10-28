package com.example.SIMATS.ui.medicalreports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.MedicalReportItem
import com.example.SIMATS.data.model.ReportCategory
import com.example.SIMATS.data.repository.MedicalReportsRepository
import com.example.SIMATS.data.repository.MedicalReportsRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MedicalReportsViewModel(
    private val repository: MedicalReportsRepository = MedicalReportsRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicalReportsUiState())
    val uiState: StateFlow<MedicalReportsUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    fun onEvent(event: MedicalReportsUiEvent) {
        when (event) {
            is MedicalReportsUiEvent.OnCategorySelected -> {
                _uiState.value = _uiState.value.copy(selectedCategory = event.category)
            }
            is MedicalReportsUiEvent.OnViewReportClicked -> {}
            is MedicalReportsUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            MedicalReportsUiEvent.OnChooseFileClicked -> {
                chooseFile()
            }
            MedicalReportsUiEvent.OnBackClicked -> {}
            MedicalReportsUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadReports() {
        viewModelScope.launch {
            repository.getReports().collect { list ->
                _uiState.value = _uiState.value.copy(reports = list)
            }
        }
    }

    private fun chooseFile() {
        _uiState.value = _uiState.value.copy(isUploading = true)
        viewModelScope.launch {
            repository.uploadReport("New_Report.pdf").collect { result ->
                result.onSuccess {
                    val newReport = MedicalReportItem(
                        id = System.currentTimeMillis().toString(),
                        title = "Lab_Results_Oct.pdf",
                        dateAndSizeText = "29 Oct 2026 · PDF · 1.2 MB",
                        category = ReportCategory.LAB_RESULTS,
                        fileType = "PDF"
                    )
                    _uiState.value = _uiState.value.copy(
                        isUploading = false,
                        reports = listOf(newReport) + _uiState.value.reports
                    )
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isUploading = false, errorMessage = err.message)
                }
            }
        }
    }

    class Factory(private val repository: MedicalReportsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MedicalReportsViewModel(repository) as T
        }
    }
}
