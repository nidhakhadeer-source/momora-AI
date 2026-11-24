package com.example.SIMATS.ui.managepatients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.ManagePatientsRepository
import com.example.SIMATS.data.repository.ManagePatientsRepositoryImpl
import com.example.SIMATS.data.repository.NotificationRepository
import com.example.SIMATS.data.repository.NotificationRepositoryImpl
import com.example.SIMATS.data.repository.NotificationStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManagePatientsViewModel(
    private val repository: ManagePatientsRepository = ManagePatientsRepositoryImpl(),
    private val notificationRepository: NotificationRepository = NotificationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManagePatientsUiState())
    val uiState: StateFlow<ManagePatientsUiState> = _uiState.asStateFlow()

    init {
        loadPatients()
        loadNotifications()
    }

    fun onEvent(event: ManagePatientsUiEvent) {
        when (event) {
            is ManagePatientsUiEvent.OnSearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is ManagePatientsUiEvent.OnFilterTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedFilterTab = event.index)
            }
            is ManagePatientsUiEvent.OnPatientCardClicked -> {
                _uiState.value = _uiState.value.copy(
                    selectedPatient = event.patient,
                    showPatientDetailsDialog = true
                )
            }
            is ManagePatientsUiEvent.OnDismissPatientDetailsDialog -> {
                _uiState.value = _uiState.value.copy(
                    showPatientDetailsDialog = false,
                    selectedPatient = null
                )
            }
            is ManagePatientsUiEvent.OnRequestToggleAccess -> {
                _uiState.value = _uiState.value.copy(
                    patientToToggle = event.patient,
                    showToggleConfirmDialog = true
                )
            }
            is ManagePatientsUiEvent.OnDismissToggleConfirmDialog -> {
                _uiState.value = _uiState.value.copy(
                    showToggleConfirmDialog = false,
                    patientToToggle = null
                )
            }
            ManagePatientsUiEvent.OnConfirmToggleAccess -> {
                val patient = _uiState.value.patientToToggle
                if (patient != null) {
                    toggleAccess(patient.id)
                }
            }
            ManagePatientsUiEvent.OnDismissFeedbackMessage -> {
                _uiState.value = _uiState.value.copy(actionFeedbackMessage = null)
            }
            is ManagePatientsUiEvent.OnEditAccountClicked -> {
                val patient = _uiState.value.patients.find { it.id == event.patientId }
                if (patient != null) {
                    _uiState.value = _uiState.value.copy(
                        selectedPatient = patient,
                        showPatientDetailsDialog = true
                    )
                }
            }
            is ManagePatientsUiEvent.OnManageAccessClicked -> {
                val patient = _uiState.value.patients.find { it.id == event.patientId }
                if (patient != null) {
                    _uiState.value = _uiState.value.copy(
                        patientToToggle = patient,
                        showToggleConfirmDialog = true
                    )
                } else {
                    toggleAccess(event.patientId)
                }
            }
            is ManagePatientsUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            ManagePatientsUiEvent.OnNextPageClicked -> {}
            ManagePatientsUiEvent.OnBackClicked -> {}
            ManagePatientsUiEvent.OnNotificationClicked -> {
                _uiState.value = _uiState.value.copy(showNotificationsDialog = true)
                markNotificationsRead()
            }
            ManagePatientsUiEvent.OnDismissNotificationsDialog -> {
                _uiState.value = _uiState.value.copy(showNotificationsDialog = false)
            }
            ManagePatientsUiEvent.OnRetryClicked -> {
                loadPatients()
                loadNotifications()
            }
        }
    }

    fun loadNotifications() {
        _uiState.value = _uiState.value.copy(isLoadingNotifications = true)
        viewModelScope.launch {
            notificationRepository.getNotifications().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val unread = list.count { !it.isRead }
                        NotificationStateManager.setUnreadCount(unread)
                        _uiState.value = _uiState.value.copy(
                            isLoadingNotifications = false,
                            notificationsList = list,
                            unreadNotificationCount = unread
                        )
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(isLoadingNotifications = false)
                    }
                )
            }
        }
    }

    private fun markNotificationsRead() {
        viewModelScope.launch {
            notificationRepository.markAllAsRead().collect { result ->
                if (result.isSuccess) {
                    NotificationStateManager.markAllRead()
                    _uiState.value = _uiState.value.copy(
                        unreadNotificationCount = 0,
                        notificationsList = _uiState.value.notificationsList.map { it.copy(isRead = true) }
                    )
                }
            }
        }
    }

    fun loadPatients() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientAccounts().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val active = list.count { it.isActive }
                        val inactive = list.count { !it.isActive }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            patients = list,
                            totalCount = list.size,
                            activeCount = active,
                            inactiveCount = inactive
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load patient accounts"
                        )
                    }
                )
            }
        }
    }

    private fun toggleAccess(id: String) {
        _uiState.value = _uiState.value.copy(isTogglingAccess = true)
        viewModelScope.launch {
            repository.togglePatientAccess(id).collect { result ->
                result.fold(
                    onSuccess = {
                        val updated = _uiState.value.patients.map { p ->
                            if (p.id == id) {
                                val newActive = !p.isActive
                                val docName = p.assignedDoctor ?: "Dr Amelia Chen"
                                p.copy(
                                    isActive = newActive,
                                    assignedDoctorText = if (newActive) "Assigned doctor: $docName" else "Account access disabled"
                                )
                            } else p
                        }
                        val active = updated.count { it.isActive }
                        val inactive = updated.count { !it.isActive }
                        val targetPatient = updated.find { it.id == id }
                        val updatedSelected = if (_uiState.value.selectedPatient?.id == id) targetPatient else _uiState.value.selectedPatient
                        val statusLabel = if (targetPatient?.isActive == true) "activated" else "deactivated"

                        _uiState.value = _uiState.value.copy(
                            patients = updated,
                            totalCount = updated.size,
                            activeCount = active,
                            inactiveCount = inactive,
                            selectedPatient = updatedSelected,
                            isTogglingAccess = false,
                            showToggleConfirmDialog = false,
                            patientToToggle = null,
                            actionFeedbackMessage = "${targetPatient?.name ?: "Patient"} account has been $statusLabel."
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isTogglingAccess = false,
                            showToggleConfirmDialog = false,
                            patientToToggle = null,
                            errorMessage = error.localizedMessage ?: "Failed to update account access"
                        )
                    }
                )
            }
        }
    }

    class Factory(private val repository: ManagePatientsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManagePatientsViewModel(repository) as T
        }
    }
}
