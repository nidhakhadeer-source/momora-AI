package com.example.SIMATS.ui.medication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.local.MedicationNotificationScheduler
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.repository.MedicationRepository
import com.example.SIMATS.data.repository.MedicationRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MedicationRemindersViewModel(
    private val repository: MedicationRepository = MedicationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        MedicationRemindersUiState(
            isNotificationsEnabled = SessionManager.getReminderMedication()
        )
    )
    val uiState: StateFlow<MedicationRemindersUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun onEvent(event: MedicationRemindersUiEvent) {
        when (event) {
            is MedicationRemindersUiEvent.OnTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedTab = event.index)
            }
            is MedicationRemindersUiEvent.OnScheduleDaySelected -> {
                _uiState.value = _uiState.value.copy(selectedScheduleDayIndex = event.index)
            }
            is MedicationRemindersUiEvent.OnMarkAsTakenClicked -> {
                markTaken(event.medicationId)
            }
            is MedicationRemindersUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            is MedicationRemindersUiEvent.OnAddMedicationClicked -> {
                _uiState.value = _uiState.value.copy(
                    showAddEditDialog = true,
                    medicationToEdit = null
                )
            }
            is MedicationRemindersUiEvent.OnEditMedicationClicked -> {
                _uiState.value = _uiState.value.copy(
                    showAddEditDialog = true,
                    medicationToEdit = event.medication
                )
            }
            is MedicationRemindersUiEvent.OnDismissAddEditDialog -> {
                _uiState.value = _uiState.value.copy(
                    showAddEditDialog = false,
                    medicationToEdit = null
                )
            }
            is MedicationRemindersUiEvent.OnDiscontinueMedicationClicked -> {
                _uiState.value = _uiState.value.copy(
                    showDiscontinueConfirmDialog = true,
                    medicationToDiscontinue = event.medication
                )
            }
            is MedicationRemindersUiEvent.OnDismissDiscontinueDialog -> {
                _uiState.value = _uiState.value.copy(
                    showDiscontinueConfirmDialog = false,
                    medicationToDiscontinue = null
                )
            }
            is MedicationRemindersUiEvent.OnConfirmDiscontinue -> {
                confirmDiscontinue()
            }
            is MedicationRemindersUiEvent.OnSaveMedication -> {
                saveMedication(
                    medicationId = event.medicationId,
                    name = event.name,
                    dosage = event.dosage,
                    frequency = event.frequency,
                    reminderTime = event.reminderTime,
                    instructions = event.instructions,
                    startDate = event.startDate,
                    endDate = event.endDate,
                    doctorName = event.doctorName
                )
            }
            is MedicationRemindersUiEvent.OnNotificationClicked -> {
                _uiState.value = _uiState.value.copy(
                    showNotificationToggleDialog = true,
                    isNotificationsEnabled = SessionManager.getReminderMedication()
                )
            }
            is MedicationRemindersUiEvent.OnDismissNotificationDialog -> {
                _uiState.value = _uiState.value.copy(showNotificationToggleDialog = false)
            }
            is MedicationRemindersUiEvent.OnToggleNotifications -> {
                toggleNotifications(event.enabled)
            }
            is MedicationRemindersUiEvent.OnDismissPermissionRationale -> {
                _uiState.value = _uiState.value.copy(showPermissionRationaleDialog = false)
            }
            is MedicationRemindersUiEvent.OnPermissionGranted -> {
                _uiState.value = _uiState.value.copy(
                    showPermissionRationaleDialog = false,
                    isNotificationsEnabled = true,
                    userActionMessage = "Medication reminders scheduled"
                )
                SessionManager.setReminderMedication(true)
                _uiState.value.data?.let {
                    MedicationNotificationScheduler.rescheduleAll(null, it.medications)
                }
            }
            is MedicationRemindersUiEvent.OnPermissionDenied -> {
                _uiState.value = _uiState.value.copy(
                    showPermissionRationaleDialog = true,
                    userActionMessage = "Notification permission required for medication reminders"
                )
            }
            is MedicationRemindersUiEvent.OnRetryClicked -> {
                loadData()
            }
            is MedicationRemindersUiEvent.OnClearUserMessage -> {
                _uiState.value = _uiState.value.copy(userActionMessage = null)
            }
            is MedicationRemindersUiEvent.OnBackClicked -> {}
        }
    }

    private fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getMedicationReminders().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _uiState.value = _uiState.value.copy(
                            data = data,
                            isLoading = false,
                            errorMessage = null
                        )
                        // Reschedule active notifications
                        MedicationNotificationScheduler.rescheduleAll(null, data.medications)
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load medication reminders"
                        )
                    }
                )
            }
        }
    }

    private fun markTaken(id: Int) {
        val currentData = _uiState.value.data ?: return
        val item = currentData.medications.firstOrNull { it.id == id } ?: return
        if (item.isTaken) return // Prevent duplicate marking

        val nowStr = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        val noteStr = "Marked taken today at $nowStr"

        // Optimistically update UI
        val updatedMeds = currentData.medications.map { med ->
            if (med.id == id) {
                med.copy(isTaken = true, takenTimeNote = noteStr, dueLaterText = null)
            } else med
        }
        val takenCount = updatedMeds.count { it.isTaken }
        val totalCount = updatedMeds.size
        val remaining = (totalCount - takenCount).coerceAtLeast(0)
        val progressText = if (remaining == 0) {
            "$totalCount OF $totalCount TAKEN · ALL COMPLETED TODAY"
        } else {
            "$takenCount OF $totalCount TAKEN · $remaining REMAINING TODAY"
        }

        _uiState.value = _uiState.value.copy(
            data = currentData.copy(
                progressStatusText = progressText,
                takenCount = takenCount,
                totalCount = totalCount,
                medications = updatedMeds
            ),
            userActionMessage = "${item.name} marked as taken"
        )

        viewModelScope.launch {
            repository.markAsTaken(id).collect { result ->
                result.onSuccess {
                    // Refresh data from server to pull updated history log
                    repository.getMedicationReminders().collect { freshRes ->
                        freshRes.onSuccess { freshData ->
                            _uiState.value = _uiState.value.copy(data = freshData)
                        }
                    }
                }
            }
        }
    }

    private fun saveMedication(
        medicationId: Int?,
        name: String,
        dosage: String,
        frequency: String,
        reminderTime: String,
        instructions: String?,
        startDate: String?,
        endDate: String?,
        doctorName: String?
    ) {
        _uiState.value = _uiState.value.copy(isSubmittingAction = true)
        viewModelScope.launch {
            val flow = if (medicationId == null) {
                repository.addMedication(
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    reminderTime = reminderTime,
                    instructions = instructions,
                    startDate = startDate,
                    endDate = endDate,
                    doctorName = doctorName
                )
            } else {
                repository.editMedication(
                    medicationId = medicationId,
                    name = name,
                    dosage = dosage,
                    frequency = frequency,
                    reminderTime = reminderTime,
                    instructions = instructions,
                    startDate = startDate,
                    endDate = endDate,
                    doctorName = doctorName
                )
            }

            flow.collect { result ->
                _uiState.value = _uiState.value.copy(isSubmittingAction = false)
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            showAddEditDialog = false,
                            medicationToEdit = null,
                            userActionMessage = if (medicationId == null) "Prescribed medication added" else "Medication schedule updated"
                        )
                        loadData()
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            userActionMessage = err.message ?: "Failed to save medication"
                        )
                    }
                )
            }
        }
    }

    private fun confirmDiscontinue() {
        val item = _uiState.value.medicationToDiscontinue ?: return
        _uiState.value = _uiState.value.copy(isSubmittingAction = true)
        viewModelScope.launch {
            repository.discontinueMedication(item.id).collect { result ->
                _uiState.value = _uiState.value.copy(
                    isSubmittingAction = false,
                    showDiscontinueConfirmDialog = false,
                    medicationToDiscontinue = null
                )
                result.fold(
                    onSuccess = {
                        MedicationNotificationScheduler.cancelReminder(null, item.id)
                        _uiState.value = _uiState.value.copy(
                            userActionMessage = "${item.name} discontinued from active schedule"
                        )
                        loadData()
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            userActionMessage = err.message ?: "Failed to discontinue medication"
                        )
                    }
                )
            }
        }
    }

    private fun toggleNotifications(enabled: Boolean) {
        SessionManager.setReminderMedication(enabled)
        _uiState.value = _uiState.value.copy(
            isNotificationsEnabled = enabled,
            showNotificationToggleDialog = false,
            userActionMessage = if (enabled) "Medication reminder notifications enabled" else "Medication notifications disabled"
        )

        val meds = _uiState.value.data?.medications ?: emptyList()
        if (enabled) {
            if (!MedicationNotificationScheduler.isNotificationPermissionGranted()) {
                _uiState.value = _uiState.value.copy(showPermissionRationaleDialog = true)
            } else {
                MedicationNotificationScheduler.rescheduleAll(null, meds)
            }
        } else {
            MedicationNotificationScheduler.rescheduleAll(null, meds)
        }
    }

    class Factory(private val repository: MedicationRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MedicationRemindersViewModel(repository) as T
        }
    }
}
