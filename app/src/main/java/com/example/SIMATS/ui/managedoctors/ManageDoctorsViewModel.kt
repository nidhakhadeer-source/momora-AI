package com.example.SIMATS.ui.managedoctors

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.DoctorAccountItem
import com.example.SIMATS.data.repository.ManageDoctorsRepository
import com.example.SIMATS.data.repository.ManageDoctorsRepositoryImpl
import com.example.SIMATS.data.repository.NotificationRepository
import com.example.SIMATS.data.repository.NotificationRepositoryImpl
import com.example.SIMATS.data.repository.NotificationStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ManageDoctorsViewModel(
    private val repository: ManageDoctorsRepository = ManageDoctorsRepositoryImpl(),
    private val notificationRepository: NotificationRepository = NotificationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ManageDoctorsUiState())
    val uiState: StateFlow<ManageDoctorsUiState> = _uiState.asStateFlow()

    init {
        loadDoctors()
        loadNotifications()
    }

    fun onEvent(event: ManageDoctorsUiEvent) {
        when (event) {
            is ManageDoctorsUiEvent.OnSearchQueryChanged -> {
                _uiState.value = _uiState.value.copy(searchQuery = event.query)
            }
            is ManageDoctorsUiEvent.OnFilterTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedFilterTab = event.index)
            }
            is ManageDoctorsUiEvent.OnToggleDoctorStatusClicked -> {
                val doc = _uiState.value.doctors.find { it.id == event.doctorId }
                if (doc != null) {
                    _uiState.value = _uiState.value.copy(doctorToToggle = doc, showToggleConfirmDialog = true)
                } else {
                    toggleStatus(event.doctorId)
                }
            }
            is ManageDoctorsUiEvent.OnEditDetailsClicked -> {
                val doc = _uiState.value.doctors.find { it.id == event.doctorId }
                if (doc != null) {
                    openEditDoctor(doc)
                }
            }
            is ManageDoctorsUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            ManageDoctorsUiEvent.OnAddDoctorClicked -> {}
            ManageDoctorsUiEvent.OnNextPageClicked -> {}
            ManageDoctorsUiEvent.OnBackClicked -> {}
            ManageDoctorsUiEvent.OnNotificationClicked -> {
                _uiState.value = _uiState.value.copy(showNotificationsDialog = true)
                markNotificationsRead()
            }
            ManageDoctorsUiEvent.OnDismissNotificationsDialog -> {
                _uiState.value = _uiState.value.copy(showNotificationsDialog = false)
            }
            ManageDoctorsUiEvent.OnRetryClicked -> {
                loadDoctors()
                loadNotifications()
            }

            // Doctor Details Dialog
            is ManageDoctorsUiEvent.OnDoctorCardClicked -> {
                _uiState.value = _uiState.value.copy(
                    selectedDoctor = event.doctor,
                    showDoctorDetailsDialog = true
                )
            }
            ManageDoctorsUiEvent.OnDismissDoctorDetailsDialog -> {
                _uiState.value = _uiState.value.copy(
                    showDoctorDetailsDialog = false,
                    selectedDoctor = null
                )
            }

            // Deactivation / Activation Confirmation
            is ManageDoctorsUiEvent.OnRequestToggleStatus -> {
                _uiState.value = _uiState.value.copy(
                    doctorToToggle = event.doctor,
                    showToggleConfirmDialog = true
                )
            }
            ManageDoctorsUiEvent.OnDismissToggleConfirmDialog -> {
                _uiState.value = _uiState.value.copy(
                    showToggleConfirmDialog = false,
                    doctorToToggle = null
                )
            }
            ManageDoctorsUiEvent.OnConfirmToggleStatus -> {
                val doc = _uiState.value.doctorToToggle
                if (doc != null) {
                    toggleStatus(doc.id)
                }
            }

            // Edit Doctor Dialog
            is ManageDoctorsUiEvent.OnOpenEditDoctor -> {
                openEditDoctor(event.doctor)
            }
            ManageDoctorsUiEvent.OnDismissEditDoctorDialog -> {
                _uiState.value = _uiState.value.copy(
                    showEditDoctorDialog = false,
                    doctorToEdit = null,
                    editErrorMessage = null
                )
            }
            is ManageDoctorsUiEvent.OnEditNameChanged -> {
                _uiState.value = _uiState.value.copy(editNameInput = event.name, editErrorMessage = null)
            }
            is ManageDoctorsUiEvent.OnEditEmailChanged -> {
                _uiState.value = _uiState.value.copy(editEmailInput = event.email, editErrorMessage = null)
            }
            is ManageDoctorsUiEvent.OnEditSpecialtyChanged -> {
                _uiState.value = _uiState.value.copy(editSpecialtyInput = event.specialty, editErrorMessage = null)
            }
            is ManageDoctorsUiEvent.OnEditLicenseIdChanged -> {
                _uiState.value = _uiState.value.copy(editLicenseIdInput = event.licenseId, editErrorMessage = null)
            }
            is ManageDoctorsUiEvent.OnEditClinicChanged -> {
                _uiState.value = _uiState.value.copy(editClinicInput = event.clinic, editErrorMessage = null)
            }
            ManageDoctorsUiEvent.OnSaveDoctorDetails -> {
                saveDoctorDetails()
            }
            ManageDoctorsUiEvent.OnDismissFeedbackMessage -> {
                _uiState.value = _uiState.value.copy(actionFeedbackMessage = null)
            }
        }
    }

    private fun openEditDoctor(doctor: DoctorAccountItem) {
        val email = doctor.email
            ?: doctor.emailAndLicenseText?.substringBefore(" ·")?.trim()
            ?: ""
        val license = doctor.licenseId
            ?: doctor.emailAndLicenseText?.substringAfter(" ·")?.trim()
            ?: ""
        val specialty = doctor.specialty
            ?: doctor.specialtyAndPatientsText.substringBefore(" ·").trim()
        val clinic = doctor.clinic ?: "Willow Maternity Clinic"

        _uiState.value = _uiState.value.copy(
            doctorToEdit = doctor,
            showEditDoctorDialog = true,
            editNameInput = doctor.name,
            editEmailInput = email,
            editSpecialtyInput = specialty,
            editLicenseIdInput = license,
            editClinicInput = clinic,
            editErrorMessage = null
        )
    }

    private fun saveDoctorDetails() {
        val doc = _uiState.value.doctorToEdit ?: return
        val name = _uiState.value.editNameInput.trim()
        val email = _uiState.value.editEmailInput.trim()
        val specialty = _uiState.value.editSpecialtyInput.trim()
        val licenseId = _uiState.value.editLicenseIdInput.trim()
        val clinic = _uiState.value.editClinicInput.trim()

        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(editErrorMessage = "Please enter doctor's full name")
            return
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(editErrorMessage = "Please enter a valid work email address")
            return
        }
        if (specialty.isBlank()) {
            _uiState.value = _uiState.value.copy(editErrorMessage = "Please enter specialty")
            return
        }
        if (licenseId.isBlank()) {
            _uiState.value = _uiState.value.copy(editErrorMessage = "Please enter license ID")
            return
        }
        if (clinic.isBlank()) {
            _uiState.value = _uiState.value.copy(editErrorMessage = "Please enter clinic")
            return
        }

        _uiState.value = _uiState.value.copy(isUpdatingDoctor = true, editErrorMessage = null)
        viewModelScope.launch {
            repository.updateDoctor(doc.id, name, email, specialty, licenseId, clinic).collect { result ->
                result.fold(
                    onSuccess = {
                        _uiState.value = _uiState.value.copy(
                            isUpdatingDoctor = false,
                            showEditDoctorDialog = false,
                            doctorToEdit = null,
                            actionFeedbackMessage = "Doctor details updated successfully"
                        )
                        loadDoctors()
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isUpdatingDoctor = false,
                            editErrorMessage = err.message ?: "Failed to update doctor details"
                        )
                    }
                )
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

    fun loadDoctors() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getDoctors().collect { result ->
                result.fold(
                    onSuccess = { list ->
                        val active = list.count { it.isActive }
                        val inactive = list.count { !it.isActive }
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            doctors = list,
                            totalCount = list.size,
                            activeCount = active,
                            inactiveCount = inactive
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load doctors list"
                        )
                    }
                )
            }
        }
    }

    private fun toggleStatus(id: String) {
        val target = _uiState.value.doctors.find { it.id == id } ?: return
        val newStatus = !target.isActive
        _uiState.value = _uiState.value.copy(isTogglingStatus = true)
        viewModelScope.launch {
            repository.toggleDoctorStatus(id).collect { result ->
                result.fold(
                    onSuccess = {
                        val spec = target.specialty ?: target.specialtyAndPatientsText.substringBefore(" ·").trim()
                        val count = target.assignedPatientCount
                        val newText = if (newStatus) "$spec · $count assigned patients" else "$spec · Access disabled"
                        val updatedDoc = target.copy(
                            isActive = newStatus,
                            specialtyAndPatientsText = newText
                        )
                        val updated = _uiState.value.doctors.map { doc ->
                            if (doc.id == id) updatedDoc else doc
                        }
                        val active = updated.count { it.isActive }
                        val inactive = updated.count { !it.isActive }
                        _uiState.value = _uiState.value.copy(
                            isTogglingStatus = false,
                            showToggleConfirmDialog = false,
                            doctorToToggle = null,
                            selectedDoctor = if (_uiState.value.selectedDoctor?.id == id) updatedDoc else _uiState.value.selectedDoctor,
                            doctors = updated,
                            activeCount = active,
                            inactiveCount = inactive,
                            actionFeedbackMessage = "${target.name} is now ${if (newStatus) "Active" else "Inactive"}"
                        )
                        loadDoctors()
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isTogglingStatus = false,
                            showToggleConfirmDialog = false,
                            errorMessage = err.message ?: "Failed to update doctor status"
                        )
                    }
                )
            }
        }
    }

    class Factory(private val repository: ManageDoctorsRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ManageDoctorsViewModel(repository) as T
        }
    }
}
