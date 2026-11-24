package com.example.SIMATS.ui.adddoctor

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.AddDoctorRepository
import com.example.SIMATS.data.repository.AddDoctorRepositoryImpl
import com.example.SIMATS.data.repository.NotificationRepository
import com.example.SIMATS.data.repository.NotificationRepositoryImpl
import com.example.SIMATS.data.repository.NotificationStateManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddDoctorViewModel(
    private val repository: AddDoctorRepository = AddDoctorRepositoryImpl(),
    private val notificationRepository: NotificationRepository = NotificationRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDoctorUiState())
    val uiState: StateFlow<AddDoctorUiState> = _uiState.asStateFlow()

    init {
        loadData()
        loadNotifications()
    }

    fun onEvent(event: AddDoctorUiEvent) {
        when (event) {
            is AddDoctorUiEvent.OnNameChanged -> {
                _uiState.value = _uiState.value.copy(nameInput = event.name, errorMessage = null)
            }
            is AddDoctorUiEvent.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(emailInput = event.email, errorMessage = null)
            }
            is AddDoctorUiEvent.OnSpecialtyChanged -> {
                _uiState.value = _uiState.value.copy(specialtyInput = event.specialty, errorMessage = null)
            }
            is AddDoctorUiEvent.OnLicenseIdChanged -> {
                _uiState.value = _uiState.value.copy(licenseIdInput = event.licenseId, errorMessage = null)
            }
            is AddDoctorUiEvent.OnClinicChanged -> {
                _uiState.value = _uiState.value.copy(clinicInput = event.clinic, errorMessage = null)
            }
            is AddDoctorUiEvent.OnClinicDropdownToggled -> {
                _uiState.value = _uiState.value.copy(isClinicDropdownExpanded = event.expanded)
            }
            is AddDoctorUiEvent.OnSendActivationToggled -> {
                _uiState.value = _uiState.value.copy(isSendActivationChecked = event.isChecked)
            }
            is AddDoctorUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            AddDoctorUiEvent.OnCreateAccountClicked -> {
                createDoctorAccount()
            }
            AddDoctorUiEvent.OnBackClicked -> {}
            AddDoctorUiEvent.OnNotificationClicked -> {
                _uiState.value = _uiState.value.copy(showNotificationsDialog = true)
                markNotificationsRead()
            }
            AddDoctorUiEvent.OnDismissNotificationsDialog -> {
                _uiState.value = _uiState.value.copy(showNotificationsDialog = false)
            }
            AddDoctorUiEvent.OnDismissSuccessMessage -> {
                _uiState.value = _uiState.value.copy(successMessage = null)
            }
            AddDoctorUiEvent.OnSuccessNavigationConsumed -> {
                _uiState.value = _uiState.value.copy(
                    isCreatedSuccess = false,
                    successMessage = null
                )
            }
            AddDoctorUiEvent.OnResetForm -> {
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    isCreatedSuccess = false,
                    successMessage = null,
                    errorMessage = null,
                    nameInput = "",
                    emailInput = "",
                    specialtyInput = "",
                    licenseIdInput = "",
                    clinicInput = "",
                    isSendActivationChecked = true,
                    isClinicDropdownExpanded = false
                )
                if (_uiState.value.formData == null) {
                    loadData()
                }
            }
            AddDoctorUiEvent.OnRetryClicked -> {
                loadData()
                loadNotifications()
            }
        }
    }

    fun loadData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getDefaultDoctorFormData().collect { result ->
                result.fold(
                    onSuccess = { data ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            formData = data
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load doctor form data"
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

    private fun createDoctorAccount() {
        val name = _uiState.value.nameInput.trim()
        val email = _uiState.value.emailInput.trim()
        val specialty = _uiState.value.specialtyInput.trim()
        val licenseId = _uiState.value.licenseIdInput.trim()
        val clinic = _uiState.value.clinicInput.trim()

        // Validation
        if (name.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter doctor's full name")
            return
        }
        if (email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid work email address")
            return
        }
        if (specialty.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter specialty")
            return
        }
        if (licenseId.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter license ID")
            return
        }
        if (clinic.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please select a clinic")
            return
        }

        // Prevent duplicate submissions
        if (_uiState.value.isCreating) {
            return
        }

        _uiState.value = _uiState.value.copy(isCreating = true, errorMessage = null, successMessage = null)
        viewModelScope.launch {
            repository.createDoctorAccount(
                name = name,
                email = email,
                specialty = specialty,
                licenseId = licenseId,
                clinic = clinic,
                sendActivationEmail = _uiState.value.isSendActivationChecked
            ).collect { result ->
                result.fold(
                    onSuccess = {
                        val msg = if (_uiState.value.isSendActivationChecked) {
                            "Doctor account created & activation invitation sent successfully"
                        } else {
                            "Doctor account created successfully"
                        }
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            isCreatedSuccess = true,
                            successMessage = msg,
                            errorMessage = null,
                            nameInput = "",
                            emailInput = "",
                            specialtyInput = "",
                            licenseIdInput = "",
                            clinicInput = "",
                            isSendActivationChecked = true
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            isCreatedSuccess = false,
                            errorMessage = err.message ?: "Failed to create doctor account"
                        )
                    }
                )
            }
        }
    }

    class Factory(
        private val repository: AddDoctorRepository,
        private val notificationRepository: NotificationRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddDoctorViewModel(repository, notificationRepository) as T
        }
    }
}
