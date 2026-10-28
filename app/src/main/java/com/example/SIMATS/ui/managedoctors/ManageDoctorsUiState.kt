package com.example.SIMATS.ui.managedoctors

import com.example.SIMATS.data.model.DoctorAccountItem

data class ManageDoctorsUiState(
    val doctors: List<DoctorAccountItem> = emptyList(),
    val searchQuery: String = "",
    val selectedFilterTab: Int = 0, // 0 = All · 12, 1 = Active · 10, 2 = Inactive · 2
    val totalCount: Int = 12,
    val activeCount: Int = 10,
    val inactiveCount: Int = 2,
    val selectedNavIndex: Int = 1, // Doctors tab active (index 1)
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
