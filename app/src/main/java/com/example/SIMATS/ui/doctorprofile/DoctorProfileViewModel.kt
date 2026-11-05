package com.example.SIMATS.ui.doctorprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorProfileData
import com.example.SIMATS.data.repository.DoctorProfileRepository
import com.example.SIMATS.data.repository.DoctorProfileRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorProfileViewModel(
    private val repository: DoctorProfileRepository = DoctorProfileRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorProfileUiState())
    val uiState: StateFlow<DoctorProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun onEvent(event: DoctorProfileUiEvent) {
        when (event) {
            is DoctorProfileUiEvent.OnFullNameChanged -> {
                _uiState.value = _uiState.value.copy(fullName = event.value)
            }
            is DoctorProfileUiEvent.OnSpecialtyChanged -> {
                _uiState.value = _uiState.value.copy(specialty = event.value)
            }
            is DoctorProfileUiEvent.OnLicenseIdChanged -> {
                _uiState.value = _uiState.value.copy(licenseId = event.value)
            }
            is DoctorProfileUiEvent.OnClinicChanged -> {
                _uiState.value = _uiState.value.copy(clinic = event.value)
            }
            is DoctorProfileUiEvent.OnGenderChanged -> {
                _uiState.value = _uiState.value.copy(gender = event.value)
            }
            is DoctorProfileUiEvent.OnPhoneChanged -> {
                _uiState.value = _uiState.value.copy(phone = event.value)
            }
            is DoctorProfileUiEvent.OnAddressChanged -> {
                _uiState.value = _uiState.value.copy(address = event.value)
            }
            is DoctorProfileUiEvent.OnQualificationChanged -> {
                _uiState.value = _uiState.value.copy(qualification = event.value)
            }
            is DoctorProfileUiEvent.OnExperienceYearsChanged -> {
                _uiState.value = _uiState.value.copy(experienceYears = event.value)
            }
            is DoctorProfileUiEvent.OnPhotoUrlChanged -> {
                _uiState.value = _uiState.value.copy(photoUrl = event.value)
            }
            DoctorProfileUiEvent.OnToggleEditMode -> {
                _uiState.value = _uiState.value.copy(isEditing = !_uiState.value.isEditing)
            }
            DoctorProfileUiEvent.OnCancelEditClicked -> {
                val data = _uiState.value.profileData
                if (data != null) {
                    populateFromProfile(data)
                }
                _uiState.value = _uiState.value.copy(isEditing = false)
            }
            DoctorProfileUiEvent.OnSaveClicked -> {
                saveProfile()
            }
            DoctorProfileUiEvent.OnRetryClicked -> {
                loadProfile()
            }
            DoctorProfileUiEvent.OnLogoutClicked -> {
                SessionManager.clearSession()
                _uiState.value = _uiState.value.copy(isLoggedOut = true)
            }
            DoctorProfileUiEvent.DismissMessages -> {
                _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
            }
            is DoctorProfileUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
        }
    }

    fun loadProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getDoctorProfile().collect { result ->
                result.fold(
                    onSuccess = { profile ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            profileData = profile
                        )
                        populateFromProfile(profile)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = error.localizedMessage ?: "Failed to load doctor profile"
                        )
                    }
                )
            }
        }
    }

    private fun populateFromProfile(profile: DoctorProfileData) {
        _uiState.value = _uiState.value.copy(
            fullName = profile.fullName.ifBlank { SessionManager.getUserName() },
            email = profile.email.ifBlank { SessionManager.getUserEmail() },
            specialty = profile.specialty,
            licenseId = profile.licenseId,
            clinic = profile.clinic,
            gender = profile.gender ?: "",
            phone = profile.phone ?: "",
            address = profile.address ?: "",
            qualification = profile.qualification ?: "",
            experienceYears = profile.experienceYears?.toString() ?: "",
            photoUrl = profile.photoUrl
        )
    }

    private fun saveProfile() {
        val currentState = _uiState.value
        val expYearsInt = currentState.experienceYears.toIntOrNull()

        val updatedProfile = DoctorProfileData(
            fullName = currentState.fullName,
            email = currentState.email,
            specialty = currentState.specialty.ifBlank { "Obstetrics" },
            licenseId = currentState.licenseId,
            clinic = currentState.clinic.ifBlank { "Willow Maternity Clinic" },
            gender = currentState.gender.ifBlank { null },
            phone = currentState.phone.ifBlank { null },
            address = currentState.address.ifBlank { null },
            qualification = currentState.qualification.ifBlank { null },
            experienceYears = expYearsInt,
            photoUrl = currentState.photoUrl
        )

        _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null, successMessage = null)
        viewModelScope.launch {
            repository.updateDoctorProfile(updatedProfile).collect { result ->
                result.fold(
                    onSuccess = { savedData ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            isEditing = false,
                            isSavedSuccess = true,
                            successMessage = "Doctor profile updated successfully",
                            profileData = savedData
                        )
                        populateFromProfile(savedData)
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            errorMessage = error.localizedMessage ?: "Failed to save profile changes"
                        )
                    }
                )
            }
        }
    }
}

