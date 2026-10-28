package com.example.SIMATS.ui.profile

import com.example.SIMATS.data.model.PatientProfileData

data class ProfileSetupUiState(
    val profileData: PatientProfileData? = null,
    val fullName: String = "Maya Patel",
    val email: String = "maya.patel@example.com",
    val dateOfBirth: String = "14 May 1997",
    val bloodGroup: String = "O positive",
    val isCareTeamSharingOn: Boolean = true,
    val selectedNavIndex: Int = 4,
    val isLoading: Boolean = false,
    val isSavedSuccess: Boolean = false,
    val errorMessage: String? = null
)
