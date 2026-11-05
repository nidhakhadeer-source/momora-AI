package com.example.SIMATS.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.local.SessionManager
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
            is ProfileSetupUiEvent.OnPhoneChanged -> {
                _uiState.value = _uiState.value.copy(phone = event.phone)
            }
            is ProfileSetupUiEvent.OnDateOfBirthChanged -> {
                _uiState.value = _uiState.value.copy(dateOfBirth = event.dob)
            }
            is ProfileSetupUiEvent.OnGenderChanged -> {
                _uiState.value = _uiState.value.copy(gender = event.gender)
            }
            is ProfileSetupUiEvent.OnBloodGroupChanged -> {
                _uiState.value = _uiState.value.copy(bloodGroup = event.bloodGroup)
            }
            is ProfileSetupUiEvent.OnAddressChanged -> {
                _uiState.value = _uiState.value.copy(address = event.address)
            }
            is ProfileSetupUiEvent.OnPhotoSelected -> {
                uploadPhoto(event)
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
            ProfileSetupUiEvent.DismissMessages -> {
                _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
            }
            ProfileSetupUiEvent.OnBackClicked -> {}
            ProfileSetupUiEvent.OnNotificationClicked -> {}
            ProfileSetupUiEvent.OnReminderPreferencesClicked -> {}
            ProfileSetupUiEvent.OnExportHealthInfoClicked -> {}
            ProfileSetupUiEvent.OnLogoutClicked -> {
                SessionManager.clearSession()
                _uiState.value = _uiState.value.copy(isLoggedOut = true)
            }
            ProfileSetupUiEvent.OnRetryClicked -> {
                loadProfileData()
            }
        }
    }

    fun loadProfileData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            repository.getPatientProfile().collect { result ->
                result.fold(
                    onSuccess = { profile ->
                        val userName = profile.fullName.ifBlank { SessionManager.getUserName() }
                        val userEmail = profile.email.ifBlank { SessionManager.getUserEmail() }
                        val userPhone = profile.phone ?: SessionManager.getPhone() ?: ""
                        val userDob = profile.dateOfBirth.ifBlank { SessionManager.getDateOfBirth() }
                        val userGender = profile.gender ?: SessionManager.getGender()
                        val userBloodGroup = profile.bloodGroup.ifBlank { SessionManager.getBloodGroup() }
                        val userAddress = profile.address ?: SessionManager.getAddress() ?: ""
                        val userPhoto = profile.photoUrl ?: profile.profilePhoto ?: SessionManager.getPhotoUrl()

                        _uiState.value = _uiState.value.copy(
                            profileData = profile.copy(
                                avatarInitials = generateInitials(userName),
                                patientName = userName,
                                fullName = userName,
                                email = userEmail,
                                phone = userPhone,
                                dateOfBirth = userDob,
                                gender = userGender,
                                bloodGroup = userBloodGroup,
                                address = userAddress,
                                photoUrl = userPhoto
                            ),
                            fullName = userName,
                            email = userEmail,
                            phone = userPhone,
                            dateOfBirth = userDob,
                            gender = userGender,
                            bloodGroup = userBloodGroup,
                            address = userAddress,
                            profilePhotoUrl = userPhoto,
                            isCareTeamSharingOn = profile.isCareTeamSharingOn,
                            isLoading = false,
                            errorMessage = null
                        )
                    },
                    onFailure = { err ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = err.message ?: "Failed to load profile"
                        )
                    }
                )
            }
        }
    }

    private fun uploadPhoto(event: ProfileSetupUiEvent.OnPhotoSelected) {
        _uiState.value = _uiState.value.copy(
            localPhotoUri = event.uri,
            isUploadingPhoto = true,
            errorMessage = null,
            successMessage = null
        )
        viewModelScope.launch {
            repository.uploadProfilePhoto(event.context, event.uri).collect { result ->
                result.onSuccess { url ->
                    _uiState.value = _uiState.value.copy(
                        isUploadingPhoto = false,
                        profilePhotoUrl = url,
                        successMessage = "Profile photo uploaded successfully"
                    )
                    loadProfileData()
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        isUploadingPhoto = false,
                        errorMessage = err.message ?: "Failed to upload photo"
                    )
                }
            }
        }
    }

    private fun saveProfile() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, successMessage = null)
        viewModelScope.launch {
            val currentProfile = _uiState.value.profileData
            val initials = generateInitials(_uiState.value.fullName)
            val updatedProfile = currentProfile?.copy(
                avatarInitials = initials,
                patientName = _uiState.value.fullName,
                fullName = _uiState.value.fullName,
                email = _uiState.value.email,
                phone = _uiState.value.phone,
                dateOfBirth = _uiState.value.dateOfBirth,
                gender = _uiState.value.gender,
                bloodGroup = _uiState.value.bloodGroup,
                address = _uiState.value.address,
                isCareTeamSharingOn = _uiState.value.isCareTeamSharingOn,
                photoUrl = _uiState.value.profilePhotoUrl
            ) ?: PatientProfileData(
                avatarInitials = initials,
                patientName = _uiState.value.fullName,
                patientId = SessionManager.getPatientCode(),
                fullName = _uiState.value.fullName,
                email = _uiState.value.email,
                phone = _uiState.value.phone,
                dateOfBirth = _uiState.value.dateOfBirth,
                gender = _uiState.value.gender,
                bloodGroup = _uiState.value.bloodGroup,
                address = _uiState.value.address,
                photoUrl = _uiState.value.profilePhotoUrl,
                isCareTeamSharingOn = _uiState.value.isCareTeamSharingOn,
                careTeamDoctorName = SessionManager.getDoctorName(),
                reminderSummary = "Medication and appointment alerts"
            )

            repository.savePatientProfile(updatedProfile).collect { result ->
                result.onSuccess {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSavedSuccess = true,
                        successMessage = "Profile details saved successfully"
                    )
                    loadProfileData()
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = err.message ?: "Failed to save profile"
                    )
                }
            }
        }
    }

    private fun generateInitials(name: String): String {
        val words = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
        val initials = words.take(2).map { it.first().uppercaseChar() }.joinToString("")
        return initials.ifBlank { "PT" }
    }

    class Factory(private val repository: ProfileRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return ProfileSetupViewModel(repository) as T
        }
    }
}
