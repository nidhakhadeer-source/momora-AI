package com.example.SIMATS.ui.visitprep

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class VisitPrepViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(VisitPrepUiState())
    val uiState: StateFlow<VisitPrepUiState> = _uiState.asStateFlow()

    fun onEvent(event: VisitPrepUiEvent) {
        when (event) {
            VisitPrepUiEvent.OnEditAppointmentClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.HealthReport.route) }
            }
            VisitPrepUiEvent.OnContactDoctorClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.HealthReport.route) }
            }
            is VisitPrepUiEvent.OnToggleChecklistItem -> {
                _uiState.update { state ->
                    val updatedList = state.prepChecklist.map { item ->
                        if (item.id == event.id) {
                            val nextDone = !item.isCompleted
                            item.copy(
                                isCompleted = nextDone,
                                statusText = if (nextDone) "Done" else "To do"
                            )
                        } else item
                    }
                    state.copy(prepChecklist = updatedList)
                }
            }
            VisitPrepUiEvent.OnAddQuestionClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.HealthReport.route) }
            }
            is VisitPrepUiEvent.OnToggleReminder -> {
                _uiState.update { it.copy(isReminderEnabled = event.enabled) }
            }
            VisitPrepUiEvent.OnPrepareHealthSummaryClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.HealthReport.route) }
            }
            is VisitPrepUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            VisitPrepUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            VisitPrepUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
