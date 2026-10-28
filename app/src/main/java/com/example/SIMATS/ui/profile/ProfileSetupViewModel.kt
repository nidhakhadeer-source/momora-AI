package com.example.SIMATS.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.model.PatientProfileData
import com.example.SIMATS.data.repository.ProfileRepository
import com.example.SIMATS.data.repository.ProfileRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileSetupViewModel(
    private val repository: ProfileRepository = ProfileRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileSetupUiState())
    val uiState: StateFlow<ProfileSetupUiState> = _uiState.asStateFlow()

    init {
        loadProfileData()
    }

    fun onEvent(event: ProfileSetupUiEvent) {
        when (event) {
            is ProfileSetupUiEvent.OnFullNameChanged -> {
                _uiState.value = _uiState.value.copy(fullName = event.fullName)
            }
            is ProfileSetupUiEvent.OnEmailChanged -> {
                _uiState.value = _uiState.value.copy(email = event.email)
            }
            is ProfileSetupUiEvent.OnDateOfBirthChanged -> {
                _uiState.value = _uiState.value.copy(dateOfBirth = event.dob)
            }
            is ProfileSetupUiEvent.OnBloodGroupChanged -> {
                _uiState.value = _uiState.value.copy(bloodGroup = event.bloodGroup)
            }
            is ProfileSetupUiEvent.OnCareTeamSharingToggled -> {
                _uiState.value = _uiState.value.copy(isCareTeamSharingOn = event.enabled)
            }
            is ProfileSetupUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            ProfileSetupUiEvent.OnSaveClicked -> {
                saveProfile()
            }
            ProfileSetupUiEvent.OnBackClicked -> {}
            ProfileSetupUiEvent.OnNotificationClicked -> {}
            ProfileSetupUiEvent.OnReminderPreferencesClicked -> {}
            ProfileSetupUiEvent.OnExportHealthInfoClicked -> {}
        }
    }

    private fun loadProfileData() {
        viewModelScope.launch {
            repository.getPatientProfile().collect { profile ->
                _uiState.value = _uiState.value.copy(
                    profileData = profile,
                    fullName = profile.fullName,
                    email = profile.email,
                    dateOfBirth = profile.dateOfBirth,
                    bloodGroup = profile.bloodGroup,
                    isCareTeamSharingOn = profile.isCareTeamSharingOn
                )
            }
        }
    }

    private fun saveProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val updatedProfile = _uiState.value.profileData?.copy(
                fullName = _uiState.value.fullName,
                email = _uiState.value.email,
                dateOfBirth = _uiState.value.dateOfBirth,
                bloodGroup = _uiState.value.bloodGroup,
                isCareTeamSharingOn = _uiState.value.isCareTeamSharingOn
            ) ?: PatientProfileData(
                avatarInitials = "MP",
                patientName = _uiState.value.fullName,
                patientId = "P-13042",
                fullName = _uiState.value.fullName,
                email = _uiState.value.email,
                dateOfBirth = _uiState.value.dateOfBirth,
                bloodGroup = _uiState.value.bloodGroup,
                isCareTeamSharingOn = _uiState.value.isCareTeamSharingOn,
                careTeamDoctorName = "Dr Amelia Chen",
                reminderSummary = "Medication and appointment alerts"
            )

            repository.savePatientProfile(updatedProfile).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(isLoading = false, isSavedSuccess = true)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = err.message)
                }
            }
        }
    }

    class Factory(private val repository: ProfileRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileSetupViewModel(repository) as T
        }
    }
}
