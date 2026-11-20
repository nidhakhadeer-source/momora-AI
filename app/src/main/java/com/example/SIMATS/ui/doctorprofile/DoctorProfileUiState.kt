package com.example.SIMATS.ui.doctorprofile

import android.net.Uri
import com.example.SIMATS.data.model.DoctorProfileData

data class DoctorProfileUiState(
    val profileData: DoctorProfileData? = null,
    val fullName: String = "",
    val email: String = "",
    val specialty: String = "",
    val licenseId: String = "",
    val clinic: String = "",
    val gender: String = "",
    val phone: String = "",
    val address: String = "",
    val qualification: String = "",
    val experienceYears: String = "",
    val photoUrl: String? = null,
    val localPhotoUri: Uri? = null,
    val isUploadingPhoto: Boolean = false,
    val appTheme: String = "SYSTEM",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val isLoggedOut: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val selectedNavIndex: Int = 0
)
