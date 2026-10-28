package com.example.SIMATS.ui.admindashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.AdminRepository
import com.example.SIMATS.data.repository.AdminRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminDashboardViewModel(
    private val repository: AdminRepository = AdminRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun onEvent(event: AdminDashboardUiEvent) {
        when (event) {
            is AdminDashboardUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            AdminDashboardUiEvent.OnManageDoctorsClicked -> {}
            AdminDashboardUiEvent.OnManagePatientsClicked -> {}
            AdminDashboardUiEvent.OnAddDoctorClicked -> {}
            AdminDashboardUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            repository.getAdminDashboardData().collect { data ->
                _uiState.value = _uiState.value.copy(dashboardData = data)
            }
        }
    }

    class Factory(private val repository: AdminRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AdminDashboardViewModel(repository) as T
        }
    }
}
