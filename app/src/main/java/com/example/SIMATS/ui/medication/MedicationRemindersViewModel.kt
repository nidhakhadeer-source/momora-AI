package com.example.SIMATS.ui.medication

import androidx.lifecycle.ViewModel
import com.example.SIMATS.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MedicationRemindersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MedicationRemindersUiState())
    val uiState: StateFlow<MedicationRemindersUiState> = _uiState.asStateFlow()

    fun onEvent(event: MedicationRemindersUiEvent) {
        when (event) {
            is MedicationRemindersUiEvent.OnFilterTabSelected -> {
                _uiState.update { it.copy(selectedFilterTab = event.tab) }
            }
            is MedicationRemindersUiEvent.OnMarkAsTakenClicked -> {
                _uiState.update { state ->
                    val updatedMedications = state.medications.map { item ->
                        if (item.id == event.id) {
                            item.copy(
                                isTaken = true,
                                statusText = "Taken ✓",
                                footerNote = "Marked taken today at 6:02 PM"
                            )
                        } else item
                    }
                    state.copy(
                        medications = updatedMedications,
                        navigateToRoute = Screen.AppointmentsCalendar.route
                    )
                }
            }
            is MedicationRemindersUiEvent.OnToggleReminders -> {
                _uiState.update { it.copy(isRemindersEnabled = event.enabled) }
            }
            MedicationRemindersUiEvent.OnAddMedicineClicked -> {
                _uiState.update { it.copy(navigateToRoute = Screen.AppointmentsCalendar.route) }
            }
            is MedicationRemindersUiEvent.OnTabSelected -> {
                _uiState.update { it.copy(selectedTab = event.tabIndex) }
            }
            MedicationRemindersUiEvent.OnBackClicked -> {
                _uiState.update { it.copy(navigateBack = true) }
            }
            MedicationRemindersUiEvent.OnNavigationHandled -> {
                _uiState.update { it.copy(navigateToRoute = null, navigateBack = false) }
            }
        }
    }
}
