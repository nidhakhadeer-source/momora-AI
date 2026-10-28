package com.example.SIMATS.ui.adddoctor

import com.example.SIMATS.data.model.AddDoctorData

data class AddDoctorUiState(
    val formData: AddDoctorData? = null,
    val nameInput: String = "Dr Olivia Bennett",
    val emailInput: String = "olivia.bennett@willow.example",
    val specialtyInput: String = "Obstetrics",
    val licenseIdInput: String = "OBS-28482",
    val clinicInput: String = "Willow Maternity Clinic",
    val isSendActivationChecked: Boolean = true,
    val selectedNavIndex: Int = 1, // Doctors tab active (index 1)
    val isCreating: Boolean = false,
    val isCreatedSuccess: Boolean = false,
    val errorMessage: String? = null
)
