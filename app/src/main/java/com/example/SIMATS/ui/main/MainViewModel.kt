package com.example.SIMATS.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.SIMATS.data.repository.DoctorRepository
import com.example.SIMATS.data.repository.DoctorRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: DoctorRepository = DoctorRepositoryImpl()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun onEvent(event: MainUiEvent) {
        when (event) {
            is MainUiEvent.OnNavTabSelected -> {
                _uiState.value = _uiState.value.copy(selectedNavIndex = event.index)
            }
            is MainUiEvent.OnReviewPatientClicked -> {}
            MainUiEvent.OnViewAssignedPatientsClicked -> {}
            MainUiEvent.OnScheduleClicked -> {}
            MainUiEvent.OnNextAppointmentClicked -> {}
            MainUiEvent.OnNotificationClicked -> {}
        }
    }

    private fun loadDashboard() {
        viewModelScope.launch {
            repository.getDoctorDashboardData().collect { data ->
                _uiState.value = _uiState.value.copy(dashboardData = data)
            }
        }
    }

    class Factory(private val repository: DoctorRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(repository) as T
        }
    }
}
