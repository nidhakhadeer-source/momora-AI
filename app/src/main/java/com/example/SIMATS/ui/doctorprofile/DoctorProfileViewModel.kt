package com.example.SIMATS.ui.doctorprofile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.local.SessionManager
import com.example.SIMATS.data.model.DoctorProfileData
import com.example.SIMATS.data.repository.DoctorProfileRepository
import com.example.SIMATS.data.repository.DoctorProfileRepositoryImpl
import com.example.SIMATS.data.repository.ProfileRepository
import com.example.SIMATS.data.repository.ProfileRepositoryImpl
import com.example.SIMATS.navigation.SelectedPatientHolder
import com.example.SIMATS.ui.components.sanitizeDoctorText
import com.example.SIMATS.ui.theme.ThemeManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DoctorProfileViewModel(
    private val repository: DoctorProfileRepository = DoctorProfileRepositoryImpl(),
    private val profileRepository: ProfileRepository = ProfileRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DoctorProfileUiState(appTheme = ThemeManager.getTheme()))
    val uiState: StateFlow<DoctorProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
        viewModelScope.launch {
            ThemeManager.themeFlow.collect { theme ->
                _uiState.value = _uiState.value.copy(appTheme = theme)
            }
        }
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
            is DoctorProfileUiEvent.OnPhotoSelected -> {
                uploadPhoto(event.uri, event.context)
            }
            is DoctorProfileUiEvent.OnThemeSelected -> {
                ThemeManager.setTheme(event.theme)
                _uiState.value = _uiState.value.copy(appTheme = event.theme)
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
                SelectedPatientHolder.clear()
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
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            errorMessage = null,
            appTheme = ThemeManager.getTheme()
        )
        viewModelScope.launch {
            repository.getDoctorProfile().collect { result ->
                result.fold(
                    onSuccess = { profile ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = null,
                            profileData = profile,
                            appTheme = ThemeManager.getTheme()
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

    private fun uploadPhoto(uri: Uri, context: Context) {
        _uiState.value = _uiState.value.copy(
            localPhotoUri = uri,
            isUploadingPhoto = true,
            errorMessage = null,
            successMessage = null
        )
        viewModelScope.launch {
            profileRepository.uploadProfilePhoto(context, uri).collect { result ->
                result.fold(
                    onSuccess = { url ->
                        _uiState.value = _uiState.value.copy(
                            isUploadingPhoto = false,
                            photoUrl = url,
                            successMessage = "Profile photo updated successfully"
                        )
                        // Also persist updated photoUrl into doctor profile
                        val cur = _uiState.value
                        val updated = DoctorProfileData(
                            fullName = cur.fullName,
                            email = cur.email,
                            specialty = cur.specialty.ifBlank { "Obstetrics" },
                            licenseId = cur.licenseId,
                            clinic = cur.clinic.ifBlank { "Willow Maternity Clinic" },
                            gender = cur.gender.ifBlank { null },
                            phone = cur.phone.ifBlank { null },
                            address = cur.address.ifBlank { null },
                            qualification = cur.qualification.ifBlank { null },
                            experienceYears = cur.experienceYears.toIntOrNull(),
                            photoUrl = url
                        )
                        repository.updateDoctorProfile(updated).collect { saveResult ->
                            saveResult.onSuccess { saved ->
                                _uiState.value = _uiState.value.copy(profileData = saved)
                            }
                        }
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isUploadingPhoto = false,
                            errorMessage = err.message ?: "Failed to upload photo"
                        )
                    }
                )
            }
        }
    }

    private fun populateFromProfile(profile: DoctorProfileData) {
        _uiState.value = _uiState.value.copy(
            fullName = profile.fullName.ifBlank { SessionManager.getUserName() }.sanitizeDoctorText(),
            email = profile.email.ifBlank { SessionManager.getUserEmail() },
            specialty = profile.specialty.sanitizeDoctorText(),
            licenseId = profile.licenseId.sanitizeDoctorText(),
            clinic = profile.clinic.sanitizeDoctorText(),
            gender = profile.gender ?: "",
            phone = profile.phone ?: "",
            address = (profile.address ?: "").sanitizeDoctorText(),
            qualification = (profile.qualification ?: "").sanitizeDoctorText(),
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
