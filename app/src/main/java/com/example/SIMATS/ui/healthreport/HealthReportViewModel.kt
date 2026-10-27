package com.example.SIMATS.ui.healthreport

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class HealthReportViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HealthReportUiState())
    val uiState: StateFlow<HealthReportUiState> = _uiState.asStateFlow()

    fun onEvent(event: HealthReportUiEvent) {
        when (event) {
            is HealthReportUiEvent.OnRangeSelected -> {
                _uiState.update { it.copy(selectedTimeRange = event.range) }
            }
            is HealthReportUiEvent.OnToggleMeasurementsAndSymptoms -> {
                _uiState.update { it.copy(includeMeasurementsAndSymptoms = event.enabled) }
            }
            is HealthReportUiEvent.OnToggleSleepAndWellbeing -> {
                _uiState.update { it.copy(includeSleepAndWellbeing = event.enabled) }
            }
            is HealthReportUiEvent.OnTogglePrivateNotes -> {
                _uiState.update { it.copy(includePrivateNotes = event.enabled) }
            }
            HealthReportUiEvent.OnPreviewAndExportPdfClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.Home.route) }
            }
            is HealthReportUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            HealthReportUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            HealthReportUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
