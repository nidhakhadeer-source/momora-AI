package com.example.SIMATS.ui.profile

import android.net.Uri
import com.example.SIMATS.data.model.PatientProfileData

data class ProfileSetupUiState(
    val profileData: PatientProfileData? = null,
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val bloodGroup: String = "",
    val address: String = "",
    val profilePhotoUrl: String? = null,
    val localPhotoUri: Uri? = null,
    val isCareTeamSharingOn: Boolean = true,
    val selectedNavIndex: Int = 4,
    val isLoading: Boolean = false,
    val isUploadingPhoto: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)
